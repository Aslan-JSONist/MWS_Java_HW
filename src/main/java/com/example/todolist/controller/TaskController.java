package com.example.todolist.controller;

import com.example.todolist.model.Task;
import com.example.todolist.service.TaskService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing tasks.
 *
 * Provides CRUD endpoints for creating,
 * reading, updating and deleting tasks.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService service;

  /**
   * Creates controller with injected task service.
   *
   * @param service service layer that handles business logic
   */
  public TaskController(TaskService service) {
    this.service = service;
  }

  /**
   * Returns all tasks.
   *
   * @return list of tasks wrapped in ResponseEntity with status 200
   */
  @GetMapping
  public ResponseEntity<List<Task>> getAll() {
    return ResponseEntity.ok(service.getAll());
  }

  /**
   * Returns task by its id.
   *
   * @param id task identifier
   * @return task if found, otherwise 404 response
   */
  @GetMapping("/{id}")
  public ResponseEntity<Task> getById(@PathVariable Long id) {
    return service.getById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Task> create(@RequestBody Task task) {
    if (task.getTitle() == null || task.getTitle().isBlank()) {
      return ResponseEntity.badRequest().build();
    }
    Task created = service.create(task);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  /**
   * Updates existing task by id.
   *
   * @param id task identifier
   * @param task updated task data
   * @return updated task or 404 if not found
   */
  @PutMapping("/{id}")
  public ResponseEntity<Task> update(@PathVariable Long id,
      @RequestBody Task task) {
    return service.update(id, task)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Deletes task by id.
   *
   * @param id task identifier
   * @return 200 if deleted, 404 if task not found
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    return service.delete(id)
        ? ResponseEntity.ok().build()
        : ResponseEntity.notFound().build();
  }
}