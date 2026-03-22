package com.example.todolist.repository;

import com.example.todolist.model.TaskAttachment;
import java.util.List;
import java.util.Optional;

/**
 * Persistence for task attachment metadata.
 */
public interface TaskAttachmentRepository {

  Optional<TaskAttachment> findById(Long id);

  List<TaskAttachment> findByTaskId(Long taskId);

  TaskAttachment save(TaskAttachment attachment);

  void deleteById(Long id);
}
