package com.example.todolist.validation;

import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

/**
 * Validates {@link TaskUpdateDto} against persisted task creation time using request path id.
 */
@Component
public class DueDateNotBeforeCreationValidator
    implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {

  private final TaskRepository taskRepository;
  private final HttpServletRequest request;

  public DueDateNotBeforeCreationValidator(TaskRepository taskRepository,
      HttpServletRequest request) {
    this.taskRepository = taskRepository;
    this.request = request;
  }

  @Override
  public boolean isValid(TaskUpdateDto dto, ConstraintValidatorContext context) {
    if (dto == null || dto.getDueDate() == null) {
      return true;
    }
    Long taskId = resolveTaskId();
    if (taskId == null) {
      return true;
    }
    return taskRepository.findById(taskId)
        .map(Task::getCreatedAt)
        .map(created -> !dto.getDueDate().isBefore(created.toLocalDate()))
        .orElse(true);
  }

  private Long resolveTaskId() {
    Object attr = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    if (!(attr instanceof Map<?, ?> map) || map.get("id") == null) {
      return null;
    }
    Object id = map.get("id");
    if (id instanceof Long l) {
      return l;
    }
    if (id instanceof String s) {
      try {
        return Long.parseLong(s);
      } catch (NumberFormatException ignored) {
        return null;
      }
    }
    return null;
  }
}
