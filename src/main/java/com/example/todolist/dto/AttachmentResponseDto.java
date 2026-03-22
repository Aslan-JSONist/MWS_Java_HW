package com.example.todolist.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Metadata returned after uploading an attachment.
 */
@Schema(description = "Uploaded attachment summary")
public class AttachmentResponseDto {

  @Schema(example = "1")
  private Long id;

  @Schema(example = "notes.txt")
  private String fileName;

  @Schema(example = "1024")
  private long size;

  @Schema(example = "2026-03-22T10:00:00")
  private LocalDateTime uploadedAt;

  public AttachmentResponseDto() {
  }

  public AttachmentResponseDto(Long id, String fileName, long size, LocalDateTime uploadedAt) {
    this.id = id;
    this.fileName = fileName;
    this.size = size;
    this.uploadedAt = uploadedAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
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
}
