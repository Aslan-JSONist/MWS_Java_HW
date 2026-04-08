package com.example.todolist.dto;

/**
 * Data Transfer Object used to expose task data through the REST API.
 *
 * This class is used instead of the internal Task entity in order
 * to decouple the API layer from the persistence/business model.
 */
public class TaskDto {

  private Long id;
  private String title;
  private String description;
  private boolean completed;

  public TaskDto() {
  }

  public TaskDto(Long id, String title, String description, boolean completed) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.completed = completed;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Returns the task title.
   */
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Returns the task description.
   */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns completion status of the task.
   */
  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }
}