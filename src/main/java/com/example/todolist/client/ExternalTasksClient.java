package com.example.todolist.client;

import com.example.todolist.dto.CreatedExternalTaskResponse;
import com.example.todolist.dto.ExternalTaskCreateRequest;
import com.example.todolist.dto.ExternalTaskDto;
import com.example.todolist.exception.ExternalApiException;
import com.example.todolist.exception.TaskNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.UnknownContentTypeException;

@Service
public class ExternalTasksClient {

  private static final Logger log = LoggerFactory.getLogger(ExternalTasksClient.class);
  private static final ParameterizedTypeReference<List<ExternalTaskDto>> TASK_LIST_TYPE =
      new ParameterizedTypeReference<>() {};

  private final ClientHttpRequestFactory requestFactory;
  private final Environment environment;
  private final ObjectMapper objectMapper;

  private volatile String cachedBaseUrl;
  private volatile RestClient client;

  public ExternalTasksClient(
      ClientHttpRequestFactory externalClientHttpRequestFactory,
      Environment environment,
      ObjectMapper objectMapper) {
    this.requestFactory = externalClientHttpRequestFactory;
    this.environment = environment;
    this.objectMapper = objectMapper;
  }

  private String resolveBaseUrl() {
    String configured = environment.getProperty("app.external.base-url");
    if (configured != null && !configured.isBlank()) {
      return configured;
    }
    String port = environment.getProperty("local.server.port");
    if (port == null || "0".equals(port)) {
      port = environment.getProperty("server.port", "8081");
    }
    return "http://127.0.0.1:" + port + "/external/v1";
  }

  private RestClient client() {
    String base = resolveBaseUrl();
    RestClient c = client;
    if (c == null || !base.equals(cachedBaseUrl)) {
      synchronized (this) {
        c = client;
        if (c == null || !base.equals(cachedBaseUrl)) {
          cachedBaseUrl = base;
          c = RestClient.builder()
              .baseUrl(base)
              .requestFactory(requestFactory)
              .defaultHeader(HttpHeaders.USER_AGENT, "todolist-gateway/1.0")
              .build();
          client = c;
        }
      }
    }
    return c;
  }

  public CreatedExternalTaskResponse createTask(ExternalTaskCreateRequest body) {
    return client().post()
        .uri("/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(body)
        .exchange((request, response) -> readCreatedTask(response));
  }

  public ExternalTaskDto getTask(Long id) {
    return client().get()
        .uri("/tasks/{id}", id)
        .accept(MediaType.APPLICATION_JSON)
        .exchange((request, response) -> readTaskDtoOnGet(response, id));
  }

  public List<ExternalTaskDto> listTasks(boolean completed, int limit) {
    try {
      List<ExternalTaskDto> body = client().get()
          .uri(uriBuilder -> uriBuilder.path("/tasks")
              .queryParam("completed", completed)
              .queryParam("limit", limit)
              .build())
          .accept(MediaType.APPLICATION_JSON)
          .retrieve()
          .onStatus(status -> status.value() == 404, (request, responseBody) -> {
            byte[] bytes = readBodyBytes(responseBody);
            String detail = parseProblemDetail(bytes);
            throw new TaskNotFoundException(-1L, detail);
          })
          .onStatus(HttpStatusCode::is5xxServerError, (request, responseBody) -> {
            throw new ExternalApiException(
                responseBody.getStatusCode().value(),
                "External API returned " + responseBody.getStatusCode().value());
          })
          .body(TASK_LIST_TYPE);
      if (body == null) {
        throw new ExternalApiException(HttpStatus.OK.value(), "Empty body");
      }
      return body;
    } catch (UnknownContentTypeException ex) {
      logNonJsonBody(ex.getResponseBody());
      throw new ExternalApiException(ex.getStatusCode().value(),
          "Unexpected content type: " + ex.getContentType(), ex);
    }
  }

  public void deleteTask(Long id) {
    ResponseEntity<Void> response = client().delete()
        .uri("/tasks/{id}", id)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(status -> status.value() == 404, (request, responseBody) -> {
          byte[] bytes = readBodyBytes(responseBody);
          String detail = parseProblemDetail(bytes);
          throw new TaskNotFoundException(id, detail);
        })
        .onStatus(HttpStatusCode::is5xxServerError, (request, responseBody) -> {
          throw new ExternalApiException(
              responseBody.getStatusCode().value(),
              "External API returned " + responseBody.getStatusCode().value());
        })
        .toBodilessEntity();
    if (!HttpStatus.NO_CONTENT.equals(response.getStatusCode())) {
      throw new ExternalApiException(response.getStatusCode().value(),
          "Expected 204 No Content but got " + response.getStatusCode().value());
    }
  }

  public String probeUnstable(String mode) {
    return client().get()
        .uri(uriBuilder -> uriBuilder.path("/unstable").queryParam("mode", mode).build())
        .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML)
        .exchange((request, response) -> {
          int code = response.getStatusCode().value();
          if (code == 429) {
            String retryAfter = response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER);
            throw new ExternalApiException(code, "External API rate limited request"
                + (retryAfter != null ? " (Retry-After=" + retryAfter + ")" : ""));
          }
          if (response.getStatusCode().is5xxServerError()) {
            MediaType contentType = response.getHeaders().getContentType();
            byte[] bytes = readBodyBytes(response);
            if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
              logNonJsonBody(bytes);
            }
            throw new ExternalApiException(code, "External API returned " + code);
          }
          return "unstable probe completed";
        });
  }

  private CreatedExternalTaskResponse readCreatedTask(
      org.springframework.http.client.ClientHttpResponse response) throws IOException {
    int code = response.getStatusCode().value();
    if (code == 404) {
      byte[] bytes = readBodyBytes(response);
      String detail = parseProblemDetail(bytes);
      throw new TaskNotFoundException(-1L, detail);
    }
    if (response.getStatusCode().is5xxServerError()) {
      throw new ExternalApiException(code, "External API returned " + code);
    }
    if (code != 201) {
      throw new ExternalApiException(code, "Expected 201 Created but got " + code);
    }

    MediaType contentType = response.getHeaders().getContentType();
    if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
      logNonJsonBody(readBodyBytes(response));
      throw new ExternalApiException(code, "Unexpected content type: " + contentType);
    }

    URI location = response.getHeaders().getLocation();
    if (location == null) {
      throw new ExternalApiException(code, "Missing Location header for created task");
    }

    byte[] bytes = readBodyBytes(response);
    if (bytes.length == 0) {
      throw new ExternalApiException(code, "Empty body");
    }
    ExternalTaskDto task = objectMapper.readValue(bytes, ExternalTaskDto.class);
    return new CreatedExternalTaskResponse(task, location);
  }

  private ExternalTaskDto readTaskDtoOnGet(org.springframework.http.client.ClientHttpResponse response, Long id)
      throws IOException {
    int code = response.getStatusCode().value();
    if (code == 404) {
      byte[] bytes = readBodyBytes(response);
      String detail = parseProblemDetail(bytes);
      throw new TaskNotFoundException(id, detail);
    }
    return readTaskDto(response);
  }

  private ExternalTaskDto readTaskDto(org.springframework.http.client.ClientHttpResponse response) throws IOException {
    HttpStatusCode status = response.getStatusCode();
    int code = status.value();
    MediaType contentType = response.getHeaders().getContentType();

    if (status.is5xxServerError()) {
      throw new ExternalApiException(code, "External API returned " + code);
    }

    if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
      logNonJsonBody(readBodyBytes(response));
      throw new ExternalApiException(code, "Unexpected content type: " + contentType);
    }

    byte[] bytes = readBodyBytes(response);
    if (bytes.length == 0) {
      throw new ExternalApiException(code, "Empty body");
    }
    return objectMapper.readValue(bytes, ExternalTaskDto.class);
  }

  private static byte[] readBodyBytes(org.springframework.http.client.ClientHttpResponse response)
      throws IOException {
    if (response.getBody() == null) {
      return new byte[0];
    }
    return StreamUtils.copyToByteArray(response.getBody());
  }

  private String parseProblemDetail(byte[] bytes) throws IOException {
    if (bytes.length == 0) {
      return null;
    }
    JsonNode root = objectMapper.readTree(bytes);
    JsonNode detail = root.get("detail");
    return detail != null && detail.isTextual() ? detail.asText() : null;
  }

  private void logNonJsonBody(byte[] bytes) {
    if (bytes.length == 0) {
      return;
    }
    int max = Math.min(512, bytes.length);
    String snippet = new String(bytes, 0, max, StandardCharsets.UTF_8);
    log.warn("Unexpected non-JSON response (truncated): {}", snippet);
  }
}
