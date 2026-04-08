package com.example.todolist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void create_invalidJson_returns400() throws Exception {
    mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{invalid"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_invalidTitle_returns400() throws Exception {
    String body = """
        {
          "title": "ab",
          "description": "x",
          "dueDate": "2099-01-01",
          "priority": "LOW"
        }
        """;
    mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_invalidTitleSize_returns400() throws Exception {
    String body = """
        {
          "title": "no"
        }
        """;
    mockMvc.perform(put("/api/tasks/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest());
  }
}
