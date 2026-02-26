package com.example.todolist.config;

import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Configuration class that defines stub repository bean.
 *
 * Used to demonstrate @Bean usage and custom bean creation.
 */
@Configuration
public class RepositoryConfig {

  /**
   * Creates stub implementation of TaskRepository.
   *
   * Contains predefined tasks for testing and demonstration.
   *
   * @return TaskRepository with fixed test data
   */
  @Bean
  public TaskRepository stubTaskRepository() {
    return new TaskRepository() {

      private final List<Task> tasks = Arrays.asList(
          new Task(100L, "Stub Task 1", "Desc", false),
          new Task(101L, "Stub Task 2", "Desc", true)
      );

      public List<Task> findAll() { return tasks; }

      public Optional<Task> findById(Long id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
      }

      public Task save(Task task) { return task; }

      public void deleteById(Long id) {}
    };
  }
}