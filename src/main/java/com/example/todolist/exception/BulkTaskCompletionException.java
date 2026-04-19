package com.example.todolist.exception;

/**
 * Thrown when bulk task completion cannot be finished for all requested ids.
 */
public class BulkTaskCompletionException extends RuntimeException {

  private final Long missingTaskId;

  public BulkTaskCompletionException(Long missingTaskId) {
    super("Task not found during bulk completion: " + missingTaskId);
    this.missingTaskId = missingTaskId;
  }

  public Long getMissingTaskId() {
    return missingTaskId;
  }
}
