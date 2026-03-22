package com.example.todolist.exception;

/**
 * Thrown when a task does not exist.
 */
public class TaskNotFoundException extends RuntimeException {

  private final Long taskId;

  public TaskNotFoundException(Long taskId) {
    super("Task not found: " + taskId);
    this.taskId = taskId;
  }

  public Long getTaskId() {
    return taskId;
  }
}
