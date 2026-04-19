package com.example.todolist.service;

import com.example.todolist.client.ExternalTasksClient;
import com.example.todolist.dto.CreatedExternalTaskResponse;
import com.example.todolist.dto.ExternalTaskCreateRequest;
import com.example.todolist.dto.ExternalTaskDto;
import com.example.todolist.exception.ExternalServiceUnavailableException;
import com.example.todolist.exception.TaskNotFoundException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TasksGatewayService {

  private static final Logger log = LoggerFactory.getLogger(TasksGatewayService.class);

  private final ExternalTasksClient externalTasksClient;

  public TasksGatewayService(ExternalTasksClient externalTasksClient) {
    this.externalTasksClient = externalTasksClient;
  }

  @RateLimiter(name = "externalApi")
  @CircuitBreaker(name = "externalApi", fallbackMethod = "createTaskFallback")
  public CreatedExternalTaskResponse createTask(ExternalTaskCreateRequest body) {
    return externalTasksClient.createTask(body);
  }

  public CreatedExternalTaskResponse createTaskFallback(ExternalTaskCreateRequest body, Throwable t) {
    if (t instanceof RequestNotPermitted rnp) {
      throw rnp;
    }
    log.warn("createTask fallback title={}: {}", body.title(), t.toString());
    throw new ExternalServiceUnavailableException(
        "External tasks API is unavailable, task was not created");
  }

  @RateLimiter(name = "externalApi")
  @CircuitBreaker(name = "externalApi", fallbackMethod = "getTaskFallback")
  public ExternalTaskDto getTask(Long id) {
    return externalTasksClient.getTask(id);
  }

  public ExternalTaskDto getTaskFallback(Long id, Throwable t) {
    if (t instanceof RequestNotPermitted rnp) {
      throw rnp;
    }
    TaskNotFoundException notFound = findTaskNotFound(t);
    if (notFound != null) {
      throw notFound;
    }
    log.warn("getTask fallback id={}: {}", id, t.toString());
    return new ExternalTaskDto(id, "(degraded) unavailable", "", false);
  }

  @RateLimiter(name = "externalApi")
  @CircuitBreaker(name = "externalApi", fallbackMethod = "listTasksFallback")
  public List<ExternalTaskDto> listTasks(boolean completed, int limit) {
    return externalTasksClient.listTasks(completed, limit);
  }

  public List<ExternalTaskDto> listTasksFallback(boolean completed, int limit, Throwable t) {
    if (t instanceof RequestNotPermitted rnp) {
      throw rnp;
    }
    log.warn("listTasks fallback: {}", t.toString());
    return Collections.emptyList();
  }

  @RateLimiter(name = "externalApi")
  @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteTaskFallback")
  public void deleteTask(Long id) {
    externalTasksClient.deleteTask(id);
  }

  public void deleteTaskFallback(Long id, Throwable t) {
    if (t instanceof RequestNotPermitted rnp) {
      throw rnp;
    }
    TaskNotFoundException notFound = findTaskNotFound(t);
    if (notFound != null) {
      throw notFound;
    }
    log.warn("deleteTask fallback id={}: {}", id, t.toString());
    throw new ExternalServiceUnavailableException(
        "External tasks API is unavailable, task was not deleted");
  }

  @RateLimiter(name = "externalApi")
  @CircuitBreaker(name = "externalApi", fallbackMethod = "probeUnstableFallback")
  public Map<String, Object> probeUnstable(String mode) {
    externalTasksClient.probeUnstable(mode);
    return Map.of(
        "mode", mode,
        "fallback", false,
        "message", "External unstable endpoint responded without triggering fallback");
  }

  public Map<String, Object> probeUnstableFallback(String mode, Throwable t) {
    if (t instanceof RequestNotPermitted rnp) {
      throw rnp;
    }
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("mode", mode);
    response.put("fallback", true);
    response.put("reason", t instanceof CallNotPermittedException ? "circuit-open" : "upstream-error");
    response.put("message", "Graceful degradation response from gateway");
    response.put("cause", t.getClass().getSimpleName());
    return response;
  }

  private static TaskNotFoundException findTaskNotFound(Throwable t) {
    for (Throwable c = t; c != null; c = c.getCause()) {
      if (c instanceof TaskNotFoundException e) {
        return e;
      }
    }
    return null;
  }
}
