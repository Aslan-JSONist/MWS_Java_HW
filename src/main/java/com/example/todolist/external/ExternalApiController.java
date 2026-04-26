package com.example.todolist.external;

import com.example.todolist.dto.ExternalTaskCreateRequest;
import com.example.todolist.dto.ExternalTaskDto;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/external/v1")
public class ExternalApiController {

  private final Map<Long, ExternalTaskDto> tasks = new ConcurrentHashMap<>();
  private final AtomicLong idGen = new AtomicLong(1);

  @PostMapping("/tasks")
  public ResponseEntity<ExternalTaskDto> create(@RequestBody ExternalTaskCreateRequest body) {
    long id = idGen.getAndIncrement();
    ExternalTaskDto dto = new ExternalTaskDto(id, body.title(), body.description(), body.completed());
    tasks.put(id, dto);
    URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/external/v1/tasks/{id}")
        .buildAndExpand(id)
        .toUri();
    return ResponseEntity.created(location).contentType(MediaType.APPLICATION_JSON).body(dto);
  }

  @GetMapping("/tasks/{id}")
  public ResponseEntity<?> getById(@PathVariable Long id) {
    ExternalTaskDto dto = tasks.get(id);
    if (dto == null) {
      return notFound(id);
    }
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(dto);
  }

  @GetMapping("/tasks")
  public List<ExternalTaskDto> list(
      @RequestParam(defaultValue = "false") boolean completed,
      @RequestParam(defaultValue = "50") int limit) {
    return tasks.values().stream()
        .filter(t -> t.completed() == completed)
        .limit(Math.max(1, limit))
        .collect(Collectors.toList());
  }

  @PutMapping("/tasks/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ExternalTaskCreateRequest body) {
    if (!tasks.containsKey(id)) {
      return notFound(id);
    }
    ExternalTaskDto dto = new ExternalTaskDto(id, body.title(), body.description(), body.completed());
    tasks.put(id, dto);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(dto);
  }

  @DeleteMapping("/tasks/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    if (!tasks.containsKey(id)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .contentType(MediaType.APPLICATION_PROBLEM_JSON)
          .body(problemNotFound(id));
    }
    tasks.remove(id);
    return ResponseEntity.noContent().build();
  }

  private ResponseEntity<ProblemDetail> notFound(Long id) {
    ProblemDetail pd = problemNotFound(id);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(pd);
  }

  private static ProblemDetail problemNotFound(Long id) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Task " + id + " not found");
    pd.setTitle("Not Found");
    pd.setInstance(URI.create("/external/v1/tasks/" + id));
    return pd;
  }

  @GetMapping("/unstable")
  public ResponseEntity<String> unstable(@RequestParam String mode) throws InterruptedException {
    switch (mode) {
      case "timeout" -> {
        Thread.sleep(120_000);
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
      }
      case "500" -> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
      case "429" -> {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .header("Retry-After", "60")
            .build();
      }
      case "html" -> {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
            .contentType(MediaType.TEXT_HTML)
            .body("<html><body>error</body></html>");
      }
      default -> {
        return ResponseEntity.badRequest().body("unknown mode");
      }
    }
  }
}
