package com.example.todolist.controller;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Task;
import com.example.todolist.service.TaskService;
import com.example.todolist.validation.OnCreate;
import com.example.todolist.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for task CRUD operations.
 */
@RestController
@RequestMapping("/api/tasks")
@Validated
@Tag(name = "Tasks", description = "Task management")
public class TaskController {

  public static final String HEADER_TOTAL_COUNT = "X-Total-Count";

  private final TaskService service;
  private final TaskMapper mapper;

  public TaskController(TaskService service, TaskMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @Operation(summary = "List all tasks")
  @ApiResponse(responseCode = "200", description = "OK")
  @GetMapping
  public ResponseEntity<List<TaskResponseDto>> getAll() {
    List<TaskResponseDto> list = service.getAll().stream().map(mapper::toResponseDto).toList();
    return ResponseEntity.ok()
        .header(HEADER_TOTAL_COUNT, String.valueOf(service.count()))
        .body(list);
  }

  @Operation(summary = "Get task by id")
  @GetMapping("/{id}")
  public ResponseEntity<TaskResponseDto> getById(@PathVariable Long id) {
    Task task = service.getById(id).orElseThrow(() -> new TaskNotFoundException(id));
    return ResponseEntity.ok(mapper.toResponseDto(task));
  }

  @Operation(summary = "Create task")
  @ApiResponse(responseCode = "201", description = "Created")
  @PostMapping
  public ResponseEntity<TaskResponseDto> create(
      @Validated(OnCreate.class) @RequestBody TaskCreateDto dto) {
    Task created = service.create(mapper.toEntity(dto));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDto(created));
  }

  @Operation(summary = "Update task")
  @PutMapping("/{id}")
  public ResponseEntity<TaskResponseDto> update(
      @PathVariable Long id,
      @Validated(OnUpdate.class) @RequestBody TaskUpdateDto dto) {
    Task updated = service.update(id, dto);
    return ResponseEntity.ok(mapper.toResponseDto(updated));
  }

  @Operation(summary = "Delete task")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
