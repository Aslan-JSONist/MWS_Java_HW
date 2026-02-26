package com.example.todolist.model;

import java.util.Objects;

/**
 * Task entity represents a task in the system.
 *
 * A task contains:
 * - id — unique identifier
 * - title — short name of the task
 * - description — detailed information
 * - completed — status of task completion
 *
 * This class overrides equals, hashCode and toString
 * for proper comparison and debugging.
 */
public class Task {

  private Long id;
  private String title;
  private String description;
  private boolean completed;

  /**
   * Default constructor.
   */
  public Task() {
  }

  /**
   * Creates a new task with all fields.
   *
   * @param id task id
   * @param title task title
   * @param description task description
   * @param completed completion status
   */
  public Task(Long id, String title, String description, boolean completed) {
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  /**
   * Compares tasks by all fields.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Task task)) return false;
    return completed == task.completed &&
        Objects.equals(id, task.id) &&
        Objects.equals(title, task.title) &&
        Objects.equals(description, task.description);
  }

  /**
   * Generates hash based on task fields.
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, title, description, completed);
  }

  /**
   * Returns string representation for debugging.
   */
  @Override
  public String toString() {
    return "Task{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", description='" + description + '\'' +
        ", completed=" + completed +
        '}';
  }
}