package com.example.todolist.config;

import com.example.todolist.controller.TaskController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS and web-related MVC configuration.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
        .allowedOrigins("http://localhost:3000")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("Authorization", "Content-Type")
        .exposedHeaders(TaskController.HEADER_TOTAL_COUNT, ApiVersionFilter.HEADER_API_VERSION)
        .allowCredentials(true)
        .maxAge(3600);
  }
}
