package com.example.todolist.exception;

/**
 * Thrown when a task does not exist (local JPA or external gateway).
 */
public class TaskNotFoundException extends RuntimeException {

  private final Long taskId;
  private final String detail;

  public TaskNotFoundException(Long taskId) {
    super("Task not found: " + taskId);
    this.taskId = taskId;
    this.detail = null;
  }

  /**
   * @param detail optional message from upstream Problem Details
   */
  public TaskNotFoundException(Long taskId, String detail) {
    super(detail != null && !detail.isBlank() ? detail : "Task not found: " + taskId);
    this.taskId = taskId;
    this.detail = detail;
  }

  public Long getTaskId() {
    return taskId;
  }

  public String getDetail() {
    return detail;
  }
}

