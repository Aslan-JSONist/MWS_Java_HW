package com.example.todolist.api;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DocsController {

  @GetMapping("/docs")
  public Map<String, String> docs() {
    return Map.of("message", "restricted documentation");
  }
}
