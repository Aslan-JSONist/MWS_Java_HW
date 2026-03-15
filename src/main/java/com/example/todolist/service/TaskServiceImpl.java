package com.example.todolist.service;

import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of TaskService.
 * Contains business logic for managing tasks.
 */
@Service
public class TaskServiceImpl implements TaskService {

  private final TaskRepository repository;

  public TaskServiceImpl(TaskRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<Task> getAll() {
    return repository.findAll();
  }

  @Override
  public Optional<Task> getById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Task create(Task task) {
    return repository.save(task);
  }

  /**
   * Updates an existing task by id.
   */
  @Override
  public Task update(Long id, Task task) {

    Task existing = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Task not found"));

    existing.setTitle(task.getTitle());
    existing.setDescription(task.getDescription());
    existing.setCompleted(task.isCompleted());

    return repository.save(existing);
  }

  @Override
  public void delete(Long id) {
    repository.deleteById(id);
  }
}
