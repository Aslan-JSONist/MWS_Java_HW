package com.example.todolist.service;

import com.example.todolist.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * Service layer interface responsible for task operations.
 */
public interface TaskService {

  List<Task> getAll();

  Optional<Task> getById(Long id);

  Task create(Task task);

  Task update(Long id, Task task);

  void delete(Long id);
}