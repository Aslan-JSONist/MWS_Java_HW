package com.example.todolist.service;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Bean with request scope.
 *
 * A new instance is created for every HTTP request.
 * Contains:
 * - unique request id
 * - request start time
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST,
    proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean {

  /** Unique id of the current request */
  private final String requestId = UUID.randomUUID().toString();

  /** Time when the bean was created */
  private final LocalDateTime startTime = LocalDateTime.now();
  public String getRequestId() { return requestId; }
  public LocalDateTime getStartTime() { return startTime; }
}