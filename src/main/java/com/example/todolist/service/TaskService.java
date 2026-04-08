package com.example.todolist.service;

import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * Service layer interface responsible for task operations.
 */
public interface TaskService {

  List<Task> getAll();

  List<Task> getAllWithAttachments();

  Optional<Task> getById(Long id);

  Task create(Task task);

  Task update(Long id, TaskUpdateDto dto);

  void bulkCompleteTasks(List<Long> ids);

  void delete(Long id);

  long count();
}
