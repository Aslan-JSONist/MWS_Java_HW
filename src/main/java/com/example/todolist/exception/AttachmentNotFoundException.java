package com.example.todolist.exception;

/**
 * Thrown when an attachment does not exist.
 */
public class AttachmentNotFoundException extends RuntimeException {

  private final Long attachmentId;

  public AttachmentNotFoundException(Long attachmentId) {
    super("Attachment not found: " + attachmentId);
    this.attachmentId = attachmentId;
  }

  public Long getAttachmentId() {
    return attachmentId;
  }
}
