package com.example.todolist.controller;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TaskService taskService;

  @MockBean
  private TaskMapper taskMapper;

  @Test
  void postCreate_returns201AndResponseBody() throws Exception {
    TaskCreateDto createDto = new TaskCreateDto();
    createDto.setTitle("Prepare homework");
    createDto.setDescription("Read lecture notes");
    createDto.setDueDate(LocalDate.now().plusDays(1));
    createDto.setPriority(Priority.HIGH);
    createDto.setTags(Set.of("study"));

    Task entity = new Task();
    entity.setTitle(createDto.getTitle());
    entity.setDescription(createDto.getDescription());
    entity.setDueDate(createDto.getDueDate());
    entity.setPriority(createDto.getPriority());
    entity.setTags(createDto.getTags());

    Task created = new Task();
    created.setId(101L);
    created.setTitle(createDto.getTitle());
    created.setDescription(createDto.getDescription());
    created.setDueDate(createDto.getDueDate());
    created.setPriority(createDto.getPriority());
    created.setTags(createDto.getTags());
    created.setCreatedAt(LocalDateTime.now());
    created.setCompleted(false);

    TaskResponseDto responseDto = new TaskResponseDto();
    responseDto.setId(101L);
    responseDto.setTitle("Prepare homework");
    responseDto.setDescription("Read lecture notes");
    responseDto.setDueDate(createDto.getDueDate());
    responseDto.setPriority(Priority.HIGH);
    responseDto.setTags(Set.of("study"));
    responseDto.setCompleted(false);

    when(taskMapper.toEntity(any(TaskCreateDto.class))).thenReturn(entity);
    when(taskService.create(entity)).thenReturn(created);
    when(taskMapper.toResponseDto(created)).thenReturn(responseDto);

    String body = """
        {
          "title": "Prepare homework",
          "description": "Read lecture notes",
          "dueDate": "2099-01-01",
          "priority": "HIGH",
          "tags": ["study"]
        }
        """;

    mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(101))
        .andExpect(jsonPath("$.title").value("Prepare homework"))
        .andExpect(jsonPath("$.priority").value("HIGH"));
  }

  @Test
  void getById_returns200AndResponseBody() throws Exception {
    Task task = new Task();
    task.setId(77L);
    task.setTitle("Existing task");
    task.setDescription("Task description");
    task.setPriority(Priority.MEDIUM);
    task.setDueDate(LocalDate.of(2099, 2, 1));
    task.setCompleted(false);

    TaskResponseDto dto = new TaskResponseDto();
    dto.setId(77L);
    dto.setTitle("Existing task");
    dto.setDescription("Task description");
    dto.setPriority(Priority.MEDIUM);
    dto.setDueDate(LocalDate.of(2099, 2, 1));
    dto.setCompleted(false);

    when(taskService.getById(77L)).thenReturn(Optional.of(task));
    when(taskMapper.toResponseDto(task)).thenReturn(dto);

    mockMvc.perform(get("/api/tasks/77"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(77))
        .andExpect(jsonPath("$.title").value("Existing task"))
        .andExpect(jsonPath("$.priority").value("MEDIUM"));
  }

  @Test
  void postCreate_invalidTitle_returns400() throws Exception {
    String invalidBody = """
        {
          "title": "ab",
          "description": "Invalid title length",
          "dueDate": "2099-01-01",
          "priority": "HIGH"
        }
        """;

    mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(invalidBody))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(taskService);
  }
}
