package com.example.todolist.repository;

import com.example.todolist.config.JpaConfig;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryIntegrationTest {

  @SuppressWarnings("resource")
  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("todolist_test")
      .withUsername("test")
      .withPassword("test");

  @DynamicPropertySource
  static void configureDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
  }

  @Autowired
  private TaskRepository taskRepository;

  @Test
  @SuppressWarnings("null")
  void findTasksDueInNextSevenDays_returnsOnlyTasksInRange() {
    Task inRange = createTask("In range", LocalDate.now().plusDays(2), Priority.HIGH);
    Task outOfRange = createTask("Out of range", LocalDate.now().plusDays(15), Priority.LOW);

    List<Task> tasks = List.of(inRange, outOfRange);
    taskRepository.saveAllAndFlush(tasks);

    List<Task> result = taskRepository.findTasksDueInNextSevenDays(
        LocalDate.now(),
        LocalDate.now().plusDays(7)
    );

    assertThat(result).extracting(Task::getTitle).contains("In range");
    assertThat(result).extracting(Task::getTitle).doesNotContain("Out of range");
  }

  private Task createTask(String title, LocalDate dueDate, Priority priority) {
    Task task = new Task();
    task.setTitle(title);
    task.setDescription("Description for " + title);
    task.setCompleted(false);
    task.setDueDate(dueDate);
    task.setPriority(priority);
    task.setTags(Set.of("integration"));
    return task;
  }
}
