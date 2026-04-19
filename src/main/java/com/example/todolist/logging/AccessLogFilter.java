package com.example.todolist.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class AccessLogFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    long start = System.currentTimeMillis();
    try {
      filterChain.doFilter(request, response);
    } finally {
      long ms = System.currentTimeMillis() - start;
      String trace = TraceIdFilter.MDC_TRACE_ID;
      String traceId = org.slf4j.MDC.get(trace);
      log.info("HTTP {} {} -> status={} timeMs={} trace={}",
          request.getMethod(),
          request.getRequestURI(),
          response.getStatus(),
          ms,
          traceId != null ? traceId : "-");
    }
  }
}
