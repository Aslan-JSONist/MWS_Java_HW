package com.example.todolist.service;

import com.example.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service that demonstrates usage of @Qualifier.
 *
 * It compares the number of tasks between:
 * - primary repository (@Primary)
 * - stub repository (@Qualifier)
 *
 * Used to show difference between two bean implementations.
 */
@Service
public class TaskStatisticsService {

  private final TaskRepository primaryRepo;
  private final TaskRepository stubRepo;

  public TaskStatisticsService(TaskRepository primaryRepo,
      @Qualifier("stubTaskRepository") TaskRepository stubRepo) {
    this.primaryRepo = primaryRepo;
    this.stubRepo = stubRepo;
  }

  /**
   * Compares the number of tasks in primary and stub repositories.
   *
   * @return string with task counts from both repositories
   */
  public String compare() {
    return "Primary: " + primaryRepo.findAll().size()
        + ", Stub: " + stubRepo.findAll().size();
  }
}