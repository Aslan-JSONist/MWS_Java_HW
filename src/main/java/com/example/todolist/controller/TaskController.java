package com.example.todolist.controller;

import com.example.todolist.dto.TaskDto;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Task;
import com.example.todolist.service.TaskService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller responsible for handling HTTP requests
 * related to task management.
 *
 * The controller operates with DTO objects instead of
 * domain entities to keep API contracts independent
 * from internal application models.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService service;
  private final TaskMapper mapper;

  public TaskController(TaskService service, TaskMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  /**
   * Returns a list of all tasks.
   */
  @GetMapping
  public ResponseEntity<List<TaskDto>> getAll() {

    List<TaskDto> tasks = service.getAll()
        .stream()
        .map(mapper::toDto)
        .collect(Collectors.toList());

    return ResponseEntity.ok(tasks);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaskDto> getById(@PathVariable Long id) {

    return service.getById(id)
        .map(task -> ResponseEntity.ok(mapper.toDto(task)))
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {

    if (service.getById(id).isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    service.delete(id);

    return ResponseEntity.ok().build();
  }

  /**
   * Creates a new task.
   */

  @PutMapping("/{id}")
  public ResponseEntity<TaskDto> update(
      @PathVariable Long id,
      @RequestBody TaskDto dto) {

    if (service.getById(id).isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Task task = mapper.toEntity(dto);
    task.setId(id);   // ❗ КЛЮЧЕВАЯ СТРОКА

    Task updated = service.update(id, task);

    return ResponseEntity.ok(mapper.toDto(updated));
  }

  @PostMapping
  public ResponseEntity<TaskDto> create(@RequestBody TaskDto dto) {

    if (dto.getTitle() == null || dto.getTitle().isBlank()) {
      return ResponseEntity.badRequest().build();
    }

    Task created = service.create(mapper.toEntity(dto));

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(mapper.toDto(created));
  }
}