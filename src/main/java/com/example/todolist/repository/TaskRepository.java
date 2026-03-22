package com.example.todolist.repository;

import com.example.todolist.model.Task;
import java.util.List;
import java.util.Optional;

/**
 * TaskRepository defines CRUD operations for task persistence layer.
 */
public interface TaskRepository {

  List<Task> findAll();

  Optional<Task> findById(Long id);

  Task save(Task task);

  void deleteById(Long id);

  long count();
}
