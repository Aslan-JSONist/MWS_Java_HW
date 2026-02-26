package com.example.todolist.repository;

import com.example.todolist.model.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Primary in-memory implementation of TaskRepository.
 *
 * Used as main repository in application.
 */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

  private final Map<Long, Task> storage = new HashMap<>();
  private long idCounter = 1;

  @Override
  public List<Task> findAll() {
    return new ArrayList<>(storage.values());
  }

  @Override
  public Optional<Task> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public Task save(Task task) {
    if (task.getId() == null) {
      task.setId(idCounter++);
    }
    storage.put(task.getId(), task);
    return task;
  }

  @Override
  public void deleteById(Long id) {
    storage.remove(id);
  }
}