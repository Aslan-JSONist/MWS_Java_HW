package com.example.todolist.repository;

import com.example.todolist.model.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe in-memory implementation of TaskRepository.
 *
 * This repository stores tasks in a ConcurrentHashMap and
 * generates unique IDs using AtomicLong.
 *
 * These structures ensure safe behavior in a multi-threaded
 * environment (for example when handling multiple HTTP requests).
 */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

  private final Map<Long, Task> storage = new ConcurrentHashMap<>();
  private final AtomicLong idCounter = new AtomicLong();

  /**
   * Returns all stored tasks.
   */
  @Override
  public List<Task> findAll() {
    return new ArrayList<>(storage.values());
  }

  /**
   * Finds a task by its id.
   */
  @Override
  public Optional<Task> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  /**
   * Saves or updates a task.
   */
  @Override
  public Task save(Task task) {

    if (task.getId() == null) {
      task.setId(idCounter.incrementAndGet());
    }

    storage.put(task.getId(), task);

    return task;
  }

  /**
   * Deletes a task by id.
   */
  @Override
  public void deleteById(Long id) {
    storage.remove(id);
  }
}