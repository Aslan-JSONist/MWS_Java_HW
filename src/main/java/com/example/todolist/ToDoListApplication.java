package com.example.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Main entry point of the ToDoList Spring Boot application.
 * <p>
 * This class bootstraps the application and enables Spring Boot auto-configuration and AspectJ
 * support.
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class ToDoListApplication {

  public static void main(String[] args) {
    SpringApplication.run(ToDoListApplication.class, args);
  }
}