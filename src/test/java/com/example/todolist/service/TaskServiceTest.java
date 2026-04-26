package com.example.todolist.service;

import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class TaskServiceTest {

  @Autowired
  private TaskService taskService;

  @MockBean
  private TaskRepository taskRepository;

  @MockBean
  private TaskMapper taskMapper;

  @MockBean
  private HttpServletRequest httpServletRequest;

  @Test
  @SuppressWarnings("null")
  void update_existingTask_updatesStatusAndVerifiesInteractions() {
    // given
    Long taskId = 10L;
    Task existing = new Task();
    existing.setId(taskId);
    existing.setTitle("Task title");
    existing.setPriority(Priority.MEDIUM);
    existing.setCompleted(false);

    TaskUpdateDto updateDto = new TaskUpdateDto();
    updateDto.setCompleted(true);

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(existing));
    when(taskRepository.save(any(Task.class)))
        .thenAnswer(invocation -> (Task) invocation.getArgument(0));

    // when
    Task updated = taskService.update(taskId, updateDto);

    // then
    assertThat(updated.getId()).isEqualTo(taskId);
    verify(taskRepository).findById(taskId);
    verify(taskMapper).updateEntity(eq(updateDto), eq(existing));
    verify(taskRepository).save(existing);
  }
}
