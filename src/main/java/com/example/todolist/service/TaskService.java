package com.example.todolist.service;

import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Business logic layer for Task management.
 *
 * Demonstrates:
 * - constructor injection
 * - @PostConstruct
 * - @PreDestroy
 * - usage of scoped beans
 */
@Service
public class TaskService {

  private final TaskRepository repository;
  private final PrototypeScopedBean prototypeBean;
  private final RequestScopedBean requestBean;

  private final Map<Long, Task> taskCache = new HashMap<>();

  @Value("${app.name}")
  private String appName;

  public TaskService(TaskRepository repository,
      PrototypeScopedBean prototypeBean,
      RequestScopedBean requestBean) {

    this.repository = repository;
    this.prototypeBean = prototypeBean;
    this.requestBean = requestBean;
  }

  /**
   * Loads tasks into cache when bean is created.
   */
  @PostConstruct
  public void init() {
    repository.findAll()
        .forEach(task -> taskCache.put(task.getId(), task));

    System.out.println(appName + " cache initialized");
  }

  /**
   * Executes before bean destruction.
   * Used to log cache size or cleanup resources.
   */
  @PreDestroy
  public void destroy() {
    System.out.println("Cache size before destroy: " + taskCache.size());
  }

  public List<Task> getAll() {
    return repository.findAll();
  }

  public Optional<Task> getById(Long id) {
    return repository.findById(id);
  }

  public Task create(Task task) {

    task.setDescription(task.getDescription()
        + " | prototypeId=" + prototypeBean.generateId()
        + " | requestId=" + requestBean.getRequestId());

    return repository.save(task);
  }

  public Optional<Task> update(Long id, Task updated) {
    return repository.findById(id).map(task -> {
      task.setTitle(updated.getTitle());
      task.setDescription(updated.getDescription());
      task.setCompleted(updated.isCompleted());
      return repository.save(task);
    });
  }

  public boolean delete(Long id) {
    Optional<Task> task = repository.findById(id);
    task.ifPresent(t -> repository.deleteById(id));
    return task.isPresent();
  }
}