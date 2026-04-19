package com.example.todolist.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Adds {@code X-API-Version} to every HTTP response.
 */
@Component
@Order(Integer.MAX_VALUE - 10)
public class ApiVersionFilter extends OncePerRequestFilter {

  public static final String HEADER_API_VERSION = "X-API-Version";

  @Value("${app.api-version}")
  private String apiVersion;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    response.setHeader(HEADER_API_VERSION, apiVersion);
    filterChain.doFilter(request, response);
  }
}
