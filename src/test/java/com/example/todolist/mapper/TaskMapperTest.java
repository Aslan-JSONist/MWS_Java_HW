package com.example.todolist.mapper;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMapperTest {

  private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

  @Test
  void toEntity_setsDefaultsAndTags() {
    TaskCreateDto dto = new TaskCreateDto();
    dto.setTitle("Hello world");
    dto.setDescription("d");
    dto.setDueDate(LocalDate.now().plusDays(2));
    dto.setPriority(Priority.LOW);
    dto.setTags(Set.of("a", "b"));

    Task task = mapper.toEntity(dto);
    assertThat(task.getId()).isNull();
    assertThat(task.isCompleted()).isFalse();
    assertThat(task.getCreatedAt()).isNull();
    assertThat(task.getTags()).containsExactlyInAnyOrder("a", "b");
  }

  @Test
  void updateEntity_partialUpdate() {
    Task task = new Task();
    task.setId(1L);
    task.setTitle("Old");
    task.setDescription("old d");
    task.setCompleted(false);
    task.setCreatedAt(LocalDateTime.of(2025, 6, 1, 10, 0));
    task.setDueDate(LocalDate.of(2025, 7, 1));
    task.setPriority(Priority.MEDIUM);
    task.setTags(Set.of("x"));

    TaskUpdateDto dto = new TaskUpdateDto();
    dto.setTitle("New title here");
    dto.setCompleted(true);

    mapper.updateEntity(dto, task);
    assertThat(task.getTitle()).isEqualTo("New title here");
    assertThat(task.isCompleted()).isTrue();
    assertThat(task.getDescription()).isEqualTo("old d");
  }

  @Test
  void toResponseDto_mapsAll() {
    Task task = new Task(1L, "t", "d", true,
        LocalDateTime.of(2024, 1, 1, 12, 0),
        LocalDate.of(2024, 2, 1),
        Priority.HIGH,
        Set.of("t1"));
    TaskResponseDto dto = mapper.toResponseDto(task);
    assertThat(dto.getId()).isEqualTo(1L);
    assertThat(dto.getTitle()).isEqualTo("t");
    assertThat(dto.isCompleted()).isTrue();
    assertThat(dto.getPriority()).isEqualTo(Priority.HIGH);
  }
}
