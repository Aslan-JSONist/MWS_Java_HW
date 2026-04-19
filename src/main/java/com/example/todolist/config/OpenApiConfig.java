package com.example.todolist.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger metadata.
 */
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI toDoOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("To-Do List API")
            .version("2.0.0")
            .description("REST API for the To-Do List Manager homework project.")
            .contact(new Contact()
                .name("MTS Java Course")
                .email("support@example.com")));
  }
}
