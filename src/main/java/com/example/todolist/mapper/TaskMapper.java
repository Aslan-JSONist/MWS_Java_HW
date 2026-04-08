package com.example.todolist.mapper;

import com.example.todolist.dto.TaskDto;
import com.example.todolist.model.Task;
import org.springframework.stereotype.Component;

/**
 * Mapper responsible for converting between
 * Task entity and TaskDto objects.
 *
 * This class isolates transformation logic between
 * internal domain models and API data structures.
 */
@Component
public class TaskMapper {

  /**
   * Converts Task entity to TaskDto.
   */
  public TaskDto toDto(Task task) {

    return new TaskDto(
        task.getId(),
        task.getTitle(),
        task.getDescription(),
        task.isCompleted()
    );
  }

  /**
   * Converts TaskDto to Task entity.
   */
  public Task toEntity(TaskDto dto) {

    return new Task(
        dto.getId(),
        dto.getTitle(),
        dto.getDescription(),
        dto.isCompleted()
    );
  }
}