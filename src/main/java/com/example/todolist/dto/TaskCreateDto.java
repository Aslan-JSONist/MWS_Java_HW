package com.example.todolist.dto;

import com.example.todolist.model.Priority;
import com.example.todolist.validation.OnCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * Payload for creating a task.
 */
@Schema(description = "Data for creating a task")
public class TaskCreateDto {

  @NotBlank(groups = OnCreate.class)
  @Size(min = 3, max = 100, groups = OnCreate.class)
  @Schema(example = "Buy groceries")
  private String title;

  @Size(max = 500, groups = OnCreate.class)
  @Schema(example = "Milk, bread")
  private String description;

  @FutureOrPresent(groups = OnCreate.class)
  @Schema(example = "2026-12-31")
  private LocalDate dueDate;

  @NotNull(groups = OnCreate.class)
  @Schema(example = "MEDIUM")
  private Priority priority;

  @Size(max = 5, groups = OnCreate.class)
  @Schema(example = "[\"home\", \"urgent\"]")
  private Set<String> tags;

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
