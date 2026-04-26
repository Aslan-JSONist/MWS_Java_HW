package com.example.todolist;

import com.example.todolist.dto.ExternalTaskCreateRequest;
import com.example.todolist.dto.ExternalTaskDto;
import com.example.todolist.dto.LoginRequest;
import com.example.todolist.dto.TokenResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GatewayApiIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private String base() {
    return "http://localhost:" + port;
  }

  private String token(String username, String password) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    LoginRequest body = new LoginRequest(username, password);
    ResponseEntity<TokenResponse> r = restTemplate.postForEntity(
        base() + "/api/v1/auth/login",
        new HttpEntity<>(body, headers),
        TokenResponse.class);
    assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(r.getBody()).isNotNull();
    return r.getBody().accessToken();
  }

  private HttpHeaders bearer(String accessToken) {
    HttpHeaders h = new HttpHeaders();
    h.setBearerAuth(accessToken);
    return h;
  }

  private void waitForLimiterWindow() throws InterruptedException {
    Thread.sleep(2200);
  }

  @Test
  @Order(30)
  void actuator_health_isReachable() {
    ResponseEntity<String> r = restTemplate.getForEntity(base() + "/actuator/health", String.class);
    assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(r.getBody()).contains("UP");
  }

  @Test
  @Order(20)
  void security_scenarios_fromHomework() {
    ResponseEntity<String> noAuth = restTemplate.getForEntity(base() + "/api/v1/profile", String.class);
    assertThat(noAuth.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    String userToken = token("user", "password");
    ResponseEntity<String> profileOk = restTemplate.exchange(
        base() + "/api/v1/profile",
        HttpMethod.GET,
        new HttpEntity<>(bearer(userToken)),
        String.class);
    assertThat(profileOk.getStatusCode()).isEqualTo(HttpStatus.OK);

    ResponseEntity<String> docsForbidden = restTemplate.exchange(
        base() + "/api/v1/docs",
        HttpMethod.GET,
        new HttpEntity<>(bearer(userToken)),
        String.class);
    assertThat(docsForbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    String readerToken = token("reader", "password");
    ResponseEntity<String> docsOk = restTemplate.exchange(
        base() + "/api/v1/docs",
        HttpMethod.GET,
        new HttpEntity<>(bearer(readerToken)),
        String.class);
    assertThat(docsOk.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @Order(10)
  void tasksGateway_crudAndNotFound() {
    String token = token("user", "password");
    HttpHeaders jsonBearer = bearer(token);
    jsonBearer.setContentType(MediaType.APPLICATION_JSON);
    jsonBearer.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

    ExternalTaskCreateRequest create = new ExternalTaskCreateRequest("t1", "d1", false);
    ResponseEntity<ExternalTaskDto> created = restTemplate.postForEntity(
        base() + "/api/v1/tasks",
        new HttpEntity<>(create, jsonBearer),
        ExternalTaskDto.class);
    assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(created.getBody()).isNotNull();
    assertThat(created.getHeaders().getLocation()).isNotNull();
    assertThat(created.getHeaders().getLocation().toString()).contains("/external/v1/tasks/");
    Long id = created.getBody().id();

    ResponseEntity<ExternalTaskDto> got = restTemplate.exchange(
        base() + "/api/v1/tasks/" + id,
        HttpMethod.GET,
        new HttpEntity<>(bearer(token)),
        ExternalTaskDto.class);
    assertThat(got.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(got.getBody().title()).isEqualTo("t1");

    ResponseEntity<String> missing = restTemplate.exchange(
        base() + "/api/v1/tasks/999999999",
        HttpMethod.GET,
        new HttpEntity<>(bearer(token)),
        String.class);
    assertThat(missing.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(missing.getBody()).contains("999999999");

    ResponseEntity<Void> del = restTemplate.exchange(
        base() + "/api/v1/tasks/" + id,
        HttpMethod.DELETE,
        new HttpEntity<>(bearer(token)),
        Void.class);
    assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ResponseEntity<ExternalTaskDto[]> list = restTemplate.exchange(
        base() + "/api/v1/tasks?completed=false&limit=10",
        HttpMethod.GET,
        new HttpEntity<>(bearer(token)),
        ExternalTaskDto[].class);
    assertThat(list.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(list.getBody()).isNotNull();
  }

  @Test
  @Order(90)
  void circuitBreaker_opensOnRepeatedUpstreamFailures() throws InterruptedException {
    waitForLimiterWindow();
    String token = token("user", "password");
    HttpEntity<Void> entity = new HttpEntity<>(bearer(token));

    boolean circuitOpened = false;
    for (int i = 0; i < 8; i++) {
      ResponseEntity<String> response = restTemplate.exchange(
          base() + "/api/v1/tasks/unstable?mode=500",
          HttpMethod.GET,
          entity,
          String.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      if (response.getBody().contains("\"reason\":\"circuit-open\"")) {
        circuitOpened = true;
        break;
      }
    }

    assertThat(circuitOpened).isTrue();
  }

  @Test
  @Order(100)
  void rateLimiter_triggersTooManyRequests() throws InterruptedException {
    waitForLimiterWindow();
    String t = token("user", "password");
    HttpHeaders jsonBearer = bearer(t);
    jsonBearer.setContentType(MediaType.APPLICATION_JSON);
    ExternalTaskCreateRequest create = new ExternalTaskCreateRequest("rl", "x", false);
    ResponseEntity<ExternalTaskDto> created = restTemplate.postForEntity(
        base() + "/api/v1/tasks",
        new HttpEntity<>(create, jsonBearer),
        ExternalTaskDto.class);
    assertThat(created.getBody()).isNotNull();
    Long id = created.getBody().id();

    HttpEntity<Void> entity = new HttpEntity<>(bearer(t));
    int tooMany = 0;
    for (int i = 0; i < 40; i++) {
      ResponseEntity<String> r = restTemplate.exchange(
          base() + "/api/v1/tasks/" + id,
          HttpMethod.GET,
          entity,
          String.class);
      if (r.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
        tooMany++;
      }
    }
    assertThat(tooMany).isPositive();
  }
}
