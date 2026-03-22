package com.example.todolist.service;

import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of TaskService.
 */
@Service
public class TaskServiceImpl implements TaskService {

  private final TaskRepository repository;
  private final TaskMapper taskMapper;

  public TaskServiceImpl(TaskRepository repository, TaskMapper taskMapper) {
    this.repository = repository;
    this.taskMapper = taskMapper;
  }

  @Override
  public List<Task> getAll() {
    return repository.findAll();
  }

  @Override
  public Optional<Task> getById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Task create(Task task) {
    return repository.save(task);
  }

  @Override
  public Task update(Long id, TaskUpdateDto dto) {
    Task existing = repository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));
    taskMapper.updateEntity(dto, existing);
    return repository.save(existing);
  }

  @Override
  public void delete(Long id) {
    if (repository.findById(id).isEmpty()) {
      throw new TaskNotFoundException(id);
    }
    repository.deleteById(id);
  }

  @Override
  public long count() {
    return repository.count();
  }
}
