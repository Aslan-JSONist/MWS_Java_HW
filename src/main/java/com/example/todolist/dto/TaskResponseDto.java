package com.example.todolist.dto;

import com.example.todolist.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Task representation returned by the API.
 */
@Schema(description = "Task data returned to the client")
public class TaskResponseDto {

  @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
  private Long id;

  @Schema(example = "Buy groceries")
  private String title;

  @Schema(example = "Milk, bread")
  private String description;

  @Schema(example = "false")
  private boolean completed;

  @Schema(example = "2026-03-22T10:00:00")
  private LocalDateTime createdAt;

  @Schema(example = "2026-12-31")
  private LocalDate dueDate;

  @Schema(example = "MEDIUM")
  private Priority priority;

  @Schema(example = "[\"home\"]")
  private Set<String> tags;

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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }
}
