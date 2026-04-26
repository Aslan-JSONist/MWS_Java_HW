package com.example.todolist.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Bean with prototype scope.
 *
 * A new instance is created every time it is requested.
 * Used to generate unique identifiers.
 */
@Component
@Scope("prototype")
public class PrototypeScopedBean {

  /**
   * Generates a unique identifier.
   *
   * @return random UUID string
   */
  public String generateId() {
    return UUID.randomUUID().toString();
  }
}