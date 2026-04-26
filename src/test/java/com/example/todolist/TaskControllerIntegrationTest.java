package com.example.todolist;

import com.example.todolist.controller.TaskController;
import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.model.Priority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskControllerIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private String url() {
    return "http://localhost:" + port + "/api/tasks";
  }

  private TaskCreateDto validCreate() {
    TaskCreateDto dto = new TaskCreateDto();
    dto.setTitle("Valid title");
    dto.setDescription("Desc");
    dto.setDueDate(LocalDate.now().plusDays(1));
    dto.setPriority(Priority.MEDIUM);
    return dto;
  }

  @Test
  void create_positive() {
    ResponseEntity<TaskResponseDto> response =
        restTemplate.postForEntity(url(), validCreate(), TaskResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    TaskResponseDto body = Objects.requireNonNull(response.getBody());
    assertThat(body.getId()).isNotNull();
    assertThat(response.getHeaders().getFirst("X-API-Version")).isEqualTo("2.0.0");
  }

  @Test
  void create_negative_validation() {
    TaskCreateDto dto = validCreate();
    dto.setTitle("ab");

    ResponseEntity<String> response =
        restTemplate.postForEntity(url(), dto, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void getAll_positive() {
    ResponseEntity<TaskResponseDto[]> response =
        restTemplate.getForEntity(url(), TaskResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getHeaders().getFirst(TaskController.HEADER_TOTAL_COUNT)).isNotNull();
    assertThat(response.getHeaders().getFirst("X-API-Version")).isEqualTo("2.0.0");
  }

  @Test
  void getAll_notEmptyAfterCreate() {
    restTemplate.postForEntity(url(), validCreate(), TaskResponseDto.class);

    ResponseEntity<TaskResponseDto[]> response =
        restTemplate.getForEntity(url(), TaskResponseDto[].class);

    assertThat(response.getBody()).isNotEmpty();
  }

  @Test
  void getById_positive() {
    TaskResponseDto created = Objects.requireNonNull(restTemplate
        .postForEntity(url(), validCreate(), TaskResponseDto.class)
        .getBody());

    ResponseEntity<TaskResponseDto> response =
        restTemplate.getForEntity(url() + "/" + created.getId(), TaskResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TaskResponseDto responseBody = Objects.requireNonNull(response.getBody());
    assertThat(responseBody.getId()).isEqualTo(created.getId());
  }

  @Test
  void getById_negative() {
    ResponseEntity<String> response =
        restTemplate.getForEntity(url() + "/999999", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void update_positive() {
    TaskResponseDto created = Objects.requireNonNull(restTemplate
        .postForEntity(url(), validCreate(), TaskResponseDto.class)
        .getBody());

    TaskUpdateDto update = new TaskUpdateDto();
    update.setTitle("Updated title longer");
    update.setCompleted(true);

    ResponseEntity<TaskResponseDto> putResponse = restTemplate.exchange(
        url() + "/" + created.getId(),
        HttpMethod.PUT,
        new HttpEntity<>(update),
        TaskResponseDto.class
    );

    assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    TaskResponseDto responseBody = Objects.requireNonNull(putResponse.getBody());
    assertThat(responseBody.getTitle()).isEqualTo("Updated title longer");
    assertThat(responseBody.isCompleted()).isTrue();
  }

  @Test
  void update_negative_notFound() {
    TaskUpdateDto update = new TaskUpdateDto();
    update.setTitle("Updated title longer");

    ResponseEntity<String> response = restTemplate.exchange(
        url() + "/999999",
        HttpMethod.PUT,
        new HttpEntity<>(update),
        String.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_positive() {
    TaskResponseDto created = Objects.requireNonNull(restTemplate
        .postForEntity(url(), validCreate(), TaskResponseDto.class)
        .getBody());

    ResponseEntity<Void> response =
        restTemplate.exchange(
            url() + "/" + created.getId(),
            HttpMethod.DELETE,
            null,
            Void.class
        );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void delete_negative() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            url() + "/999999",
            HttpMethod.DELETE,
            null,
            String.class
        );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
