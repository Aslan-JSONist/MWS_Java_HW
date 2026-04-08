package com.example.todolist.repository;

import com.example.todolist.model.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA repository for task attachment metadata.
 */
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

  List<TaskAttachment> findByTask_IdOrderByIdAsc(Long taskId);
}
