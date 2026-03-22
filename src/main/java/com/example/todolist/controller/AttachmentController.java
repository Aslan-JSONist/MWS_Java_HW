package com.example.todolist.controller;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Upload and download task attachments.
 */
@RestController
@Tag(name = "Attachments", description = "File attachments for tasks")
public class AttachmentController {

  private final AttachmentService attachmentService;

  public AttachmentController(AttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }

  @Operation(summary = "Upload attachment for task")
  @PostMapping(value = "/api/tasks/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<AttachmentResponseDto> upload(
      @PathVariable Long taskId,
      @RequestPart("file") MultipartFile file) {
    TaskAttachment saved = attachmentService.storeAttachment(taskId, file);
    AttachmentResponseDto body = new AttachmentResponseDto(
        saved.getId(),
        saved.getFileName(),
        saved.getSize(),
        saved.getUploadedAt()
    );
    return ResponseEntity.status(201).body(body);
  }

  @Operation(summary = "Download attachment")
  @GetMapping("/api/attachments/{attachmentId}")
  public ResponseEntity<Resource> download(@PathVariable Long attachmentId) {
    TaskAttachment meta = attachmentService.getAttachment(attachmentId);
    Resource resource = attachmentService.loadAsResource(attachmentId);
    ContentDisposition disposition = ContentDisposition.attachment()
        .filename(meta.getFileName())
        .build();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
        .contentType(MediaType.parseMediaType(meta.getContentType()))
        .body(resource);
  }

  @Operation(summary = "Delete attachment")
  @DeleteMapping("/api/attachments/{attachmentId}")
  public ResponseEntity<Void> delete(@PathVariable Long attachmentId) {
    attachmentService.deleteAttachment(attachmentId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "List attachments for task")
  @GetMapping("/api/tasks/{taskId}/attachments")
  public ResponseEntity<List<AttachmentResponseDto>> listForTask(@PathVariable Long taskId) {
    List<AttachmentResponseDto> list = attachmentService.listForTask(taskId).stream()
        .map(a -> new AttachmentResponseDto(a.getId(), a.getFileName(), a.getSize(), a.getUploadedAt()))
        .toList();
    return ResponseEntity.ok(list);
  }
}
