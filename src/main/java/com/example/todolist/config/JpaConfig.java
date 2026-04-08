package com.example.todolist.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing annotations such as CreatedDate and LastModifiedDate.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
