package com.example.todolist.dto;

import java.net.URI;

public record CreatedExternalTaskResponse(ExternalTaskDto task, URI location) {
}
