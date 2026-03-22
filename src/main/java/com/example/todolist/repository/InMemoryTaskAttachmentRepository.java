package com.example.todolist.repository;

import com.example.todolist.model.TaskAttachment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory implementation of TaskAttachmentRepository.
 */
@Repository
public class InMemoryTaskAttachmentRepository implements TaskAttachmentRepository {

  private final Map<Long, TaskAttachment> storage = new ConcurrentHashMap<>();
  private final AtomicLong idCounter = new AtomicLong();

  @Override
  public Optional<TaskAttachment> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public List<TaskAttachment> findByTaskId(Long taskId) {
    return storage.values().stream()
        .filter(a -> taskId.equals(a.getTaskId()))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Override
  public TaskAttachment save(TaskAttachment attachment) {
    if (attachment.getId() == null) {
      attachment.setId(idCounter.incrementAndGet());
    }
    storage.put(attachment.getId(), attachment);
    return attachment;
  }

  @Override
  public void deleteById(Long id) {
    storage.remove(id);
  }
}
