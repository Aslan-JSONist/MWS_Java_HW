package com.example.todolist.config;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Configuration class that defines stub repository bean.
 */
@Configuration
public class RepositoryConfig {

  /**
   * Creates stub implementation of TaskRepository.
   *
   * @return TaskRepository with fixed test data
   */
  @Bean
  public TaskRepository stubTaskRepository() {
    return new TaskRepository() {

      private final LocalDateTime stubTime = LocalDateTime.of(2020, 1, 1, 12, 0);

      private final List<Task> tasks = Arrays.asList(
          new Task(100L, "Stub Task 1", "Desc", false,
              stubTime, LocalDate.of(2025, 12, 31), Priority.MEDIUM, Set.of("stub")),
          new Task(101L, "Stub Task 2", "Desc", true,
              stubTime, LocalDate.of(2025, 12, 31), Priority.HIGH, Set.of())
      );

      @Override
      public List<Task> findAll() {
        return tasks;
      }

      @Override
      public Optional<Task> findById(Long id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
      }

      @Override
      public Task save(Task task) {
        return task;
      }

      @Override
      public void deleteById(Long id) {
      }

      @Override
      public long count() {
        return tasks.size();
      }
    };
  }
}
