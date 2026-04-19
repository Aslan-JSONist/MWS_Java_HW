package com.example.todolist.dto;

import com.example.todolist.model.Priority;

/**
 * Aggregated number of tasks for a single priority.
 */
public class TaskPriorityCountDto {

  private Priority priority;
  private long count;

  public TaskPriorityCountDto() {
  }

  public TaskPriorityCountDto(Priority priority, long count) {
    this.priority = priority;
    this.count = count;
  }

  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }
}
