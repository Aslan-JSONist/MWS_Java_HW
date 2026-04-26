package com.example.todolist.service;

import com.example.todolist.exception.AttachmentNotFoundException;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.repository.TaskAttachmentRepository;
import com.example.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * File-system backed attachment storage.
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {

  private final TaskRepository taskRepository;
  private final TaskAttachmentRepository attachmentRepository;
  private final Path uploadRoot;

  public AttachmentServiceImpl(
      TaskRepository taskRepository,
      TaskAttachmentRepository attachmentRepository,
      @Value("${app.upload-dir}") String uploadDir) {
    this.taskRepository = taskRepository;
    this.attachmentRepository = attachmentRepository;
    this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
  }

  @PostConstruct
  void ensureUploadDir() throws IOException {
    Files.createDirectories(uploadRoot);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TaskAttachment> listForTask(Long taskId) {
    if (taskRepository.findById(taskId).isEmpty()) {
      throw new TaskNotFoundException(taskId);
    }
    return attachmentRepository.findByTask_IdOrderByIdAsc(taskId);
  }

  @Override
  @Transactional
  public TaskAttachment storeAttachment(Long taskId, MultipartFile file) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));
    String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
    String stored = UUID.randomUUID().toString();
    Path target = uploadRoot.resolve(stored);
    try (InputStream in = file.getInputStream()) {
      Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to store file", e);
    }
    TaskAttachment meta = new TaskAttachment();
    meta.setTask(task);
    meta.setFileName(original);
    meta.setStoredFileName(stored);
    meta.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
    meta.setSize(file.getSize());
    meta.setUploadedAt(LocalDateTime.now());
    try {
      return attachmentRepository.save(meta);
    } catch (RuntimeException ex) {
      try {
        Files.deleteIfExists(target);
      } catch (IOException ignored) {
        // best effort cleanup after a failed DB operation
      }
      throw ex;
    }
  }

  @Override
  @Transactional(readOnly = true)
  public TaskAttachment getAttachment(Long attachmentId) {
    return attachmentRepository.findById(attachmentId)
        .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
  }

  @Override
  @Transactional(readOnly = true)
  public Resource loadAsResource(Long attachmentId) {
    TaskAttachment meta = getAttachment(attachmentId);
    Path file = uploadRoot.resolve(meta.getStoredFileName());
    if (!Files.exists(file)) {
      throw new AttachmentNotFoundException(attachmentId);
    }
    try {
      Resource resource = new UrlResource(file.toUri());
      if (!resource.exists() || !resource.isReadable()) {
        throw new AttachmentNotFoundException(attachmentId);
      }
      return resource;
    } catch (MalformedURLException e) {
      throw new AttachmentNotFoundException(attachmentId);
    }
  }

  @Override
  @Transactional
  public void deleteAttachment(Long attachmentId) {
    TaskAttachment meta = getAttachment(attachmentId);
    Path file = uploadRoot.resolve(meta.getStoredFileName());
    try {
      Files.deleteIfExists(file);
    } catch (IOException ignored) {
      // still remove metadata
    }
    attachmentRepository.delete(meta);
  }
}
