package com.example.todolist.api;

import com.example.todolist.dto.CreatedExternalTaskResponse;
import com.example.todolist.dto.ExternalTaskCreateRequest;
import com.example.todolist.dto.ExternalTaskDto;
import com.example.todolist.service.TasksGatewayService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
public class TasksGatewayController {

  private final TasksGatewayService tasksGatewayService;

  public TasksGatewayController(TasksGatewayService tasksGatewayService) {
    this.tasksGatewayService = tasksGatewayService;
  }

  @PostMapping
  public ResponseEntity<ExternalTaskDto> create(@RequestBody ExternalTaskCreateRequest body) {
    CreatedExternalTaskResponse created = tasksGatewayService.createTask(body);
    return ResponseEntity.created(created.location()).body(created.task());
  }

  @GetMapping("/{id}")
  public ExternalTaskDto get(@PathVariable Long id) {
    return tasksGatewayService.getTask(id);
  }

  @GetMapping
  public List<ExternalTaskDto> list(
      @RequestParam(defaultValue = "false") boolean completed,
      @RequestParam(defaultValue = "50") int limit) {
    return tasksGatewayService.listTasks(completed, limit);
  }

  @GetMapping("/unstable")
  public Map<String, Object> probeUnstable(@RequestParam String mode) {
    return tasksGatewayService.probeUnstable(mode);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    tasksGatewayService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }
}
