package com.example.todolist.service;

import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.exception.BulkTaskCompletionException;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
  @Transactional(readOnly = true)
  public List<Task> getAll() {
    return repository.findAllByOrderByIdAsc();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Task> getAllWithAttachments() {
    return repository.findAllWithAttachments();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Task> getById(Long id) {
    return repository.findById(id);
  }

  @Override
  @Transactional
  public Task create(Task task) {
    return repository.save(task);
  }

  @Override
  @Transactional
  public Task update(Long id, TaskUpdateDto dto) {
    Task existing = repository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));
    taskMapper.updateEntity(dto, existing);
    return repository.save(existing);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED,
      rollbackFor = BulkTaskCompletionException.class)
  public void bulkCompleteTasks(List<Long> ids) {
    for (Long id : ids) {
      Task task = repository.findById(id)
          .orElseThrow(() -> new BulkTaskCompletionException(id));
      task.setCompleted(true);
    }
  }

  @Override
  @Transactional
  public void delete(Long id) {
    if (repository.findById(id).isEmpty()) {
      throw new TaskNotFoundException(id);
    }
    repository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public long count() {
    return repository.count();
  }
}
