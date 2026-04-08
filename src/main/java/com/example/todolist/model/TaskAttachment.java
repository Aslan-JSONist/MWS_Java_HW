package com.example.todolist.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Metadata for a file attached to a task.
 */
@Entity
@Table(name = "task_attachments")
public class TaskAttachment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  @Column(name = "file_name", nullable = false, length = 255)
  private String fileName;

  @Column(name = "stored_file_name", nullable = false, unique = true, length = 255)
  private String storedFileName;

  @Column(name = "content_type", nullable = false, length = 255)
  private String contentType;

  @Column(nullable = false)
  private long size;

  @Column(name = "uploaded_at", nullable = false)
  private LocalDateTime uploadedAt;

  public TaskAttachment() {
  }

  public TaskAttachment(Long id, Long taskId, String fileName, String storedFileName,
      String contentType, long size, LocalDateTime uploadedAt) {
    this.id = id;
    this.fileName = fileName;
    this.storedFileName = storedFileName;
    this.contentType = contentType;
    this.size = size;
    this.uploadedAt = uploadedAt;
    setTaskId(taskId);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Task getTask() {
    return task;
  }

  public void setTask(Task task) {
    this.task = task;
  }

  public Long getTaskId() {
    return task != null ? task.getId() : null;
  }

  public void setTaskId(Long taskId) {
    if (taskId == null) {
      this.task = null;
      return;
    }
    if (this.task == null) {
      this.task = new Task();
    }
    this.task.setId(taskId);
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getStoredFileName() {
    return storedFileName;
  }

  public void setStoredFileName(String storedFileName) {
    this.storedFileName = storedFileName;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public LocalDateTime getUploadedAt() {
    return uploadedAt;
  }

  public void setUploadedAt(LocalDateTime uploadedAt) {
    this.uploadedAt = uploadedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TaskAttachment that)) {
      return false;
    }
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
