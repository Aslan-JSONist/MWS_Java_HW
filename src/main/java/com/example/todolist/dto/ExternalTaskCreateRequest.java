package com.example.todolist.dto;

public record ExternalTaskCreateRequest(String title, String description, boolean completed) {
}
