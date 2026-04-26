package com.example.todolist.dto;

public record ExternalTaskDto(Long id, String title, String description, boolean completed) {
}
