package com.example.todolist.dto;

import com.example.todolist.model.Priority;
import com.example.todolist.validation.DueDateNotBeforeCreation;
import com.example.todolist.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * Payload for partial task update. All fields are optional.
 */
@DueDateNotBeforeCreation(groups = OnUpdate.class)
@Schema(description = "Partial update for a task")
public class TaskUpdateDto {

  @Size(min = 3, max = 100, groups = OnUpdate.class)
  @Schema(example = "Updated title")
  private String title;

  @Size(max = 500, groups = OnUpdate.class)
  private String description;

  @Schema(example = "true")
  private Boolean completed;

  @Size(max = 5, groups = OnUpdate.class)
  private Set<String> tags;

  @Schema(example = "MEDIUM")
  private Priority priority;

  @Schema(example = "2026-12-31")
  private LocalDate dueDate;

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

  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }
}
