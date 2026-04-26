package com.example.todolist.service;

import com.example.todolist.model.TaskAttachment;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Stores and loads task file attachments.
 */
public interface AttachmentService {

  List<TaskAttachment> listForTask(Long taskId);

  TaskAttachment storeAttachment(Long taskId, MultipartFile file);

  TaskAttachment getAttachment(Long attachmentId);

  Resource loadAsResource(Long attachmentId);

  void deleteAttachment(Long attachmentId);
}
