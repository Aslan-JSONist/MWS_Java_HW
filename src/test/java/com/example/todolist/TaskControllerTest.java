package com.example.todolist;

import com.example.todolist.dto.TaskDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private String url() {
    return "http://localhost:" + port + "/api/tasks";
  }

  /* ===================== POST ===================== */

  @Test
  void create_positive() {
    TaskDto task = new TaskDto(null, "Title", "Desc", false);

    ResponseEntity<TaskDto> response =
        restTemplate.postForEntity(url(), task, TaskDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void create_negative() {
    TaskDto task = new TaskDto(null, "", "Desc", false);

    ResponseEntity<TaskDto> response =
        restTemplate.postForEntity(url(), task, TaskDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  /* ===================== GET ALL ===================== */

  @Test
  void getAll_positive() {
    ResponseEntity<TaskDto[]> response =
        restTemplate.getForEntity(url(), TaskDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void getAll_notEmptyAfterCreate() {
    TaskDto task = new TaskDto(null, "Test", "Desc", false);
    restTemplate.postForEntity(url(), task, TaskDto.class);

    ResponseEntity<TaskDto[]> response =
        restTemplate.getForEntity(url(), TaskDto[].class);

    assertThat(response.getBody()).isNotEmpty();
  }

  /* ===================== GET BY ID ===================== */

  @Test
  void getById_positive() {
    TaskDto task = new TaskDto(null, "Title", "Desc", false);

    TaskDto created = restTemplate
        .postForEntity(url(), task, TaskDto.class)
        .getBody();

    ResponseEntity<TaskDto> response =
        restTemplate.getForEntity(url() + "/" + created.getId(), TaskDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getId()).isEqualTo(created.getId());
  }

  @Test
  void getById_negative() {
    ResponseEntity<TaskDto> response =
        restTemplate.getForEntity(url() + "/999999", TaskDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  /* ===================== PUT ===================== */

  @Test
  void update_positive() {
    TaskDto task = new TaskDto(null, "Title", "Desc", false);

    TaskDto created = restTemplate
        .postForEntity(url(), task, TaskDto.class)
        .getBody();

    TaskDto update = new TaskDto(null, "Updated", "UpdatedDesc", true);

    restTemplate.put(url() + "/" + created.getId(), update);

    ResponseEntity<TaskDto> response =
        restTemplate.getForEntity(url() + "/" + created.getId(), TaskDto.class);

    assertThat(response.getBody().getTitle()).isEqualTo("Updated");
    assertThat(response.getBody().isCompleted()).isTrue();
  }

  @Test
  void update_negative() {
    TaskDto update = new TaskDto(null, "Updated", "UpdatedDesc", true);

    ResponseEntity<Void> response =
        restTemplate.exchange(
            url() + "/999999",
            HttpMethod.PUT,
            new HttpEntity<>(update),
            Void.class
        );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  /* ===================== DELETE ===================== */

  @Test
  void delete_positive() {
    TaskDto task = new TaskDto(null, "Title", "Desc", false);

    TaskDto created = restTemplate
        .postForEntity(url(), task, TaskDto.class)
        .getBody();

    ResponseEntity<Void> response =
        restTemplate.exchange(
            url() + "/" + created.getId(),
            HttpMethod.DELETE,
            null,
            Void.class
        );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void delete_negative() {
    ResponseEntity<Void> response =
        restTemplate.exchange(
            url() + "/999999",
            HttpMethod.DELETE,
            null,
            Void.class
        );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}