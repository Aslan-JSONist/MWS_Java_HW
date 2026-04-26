package com.example.todolist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * User view preferences stored in a cookie.
 */
@RestController
@RequestMapping("/api/preferences")
@Tag(name = "Preferences", description = "View preferences (cookie)")
public class PreferencesController {

  static final String VIEW_COOKIE = "viewPreference";

  @Operation(summary = "Get view preference")
  @GetMapping("/view")
  public ResponseEntity<Map<String, String>> getView(
      @CookieValue(value = VIEW_COOKIE, required = false) String mode,
      HttpServletResponse response) {
    String value = (mode != null && !mode.isBlank()) ? mode : "compact";
    if (mode == null) {
      ResponseCookie cookie = ResponseCookie.from(VIEW_COOKIE, value)
          .path("/")
          .httpOnly(true)
          .build();
      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
    return ResponseEntity.ok(Map.of("mode", value));
  }

  @Operation(summary = "Set view preference")
  @PostMapping("/view")
  public ResponseEntity<Map<String, String>> setView(
      @RequestParam("mode") String mode,
      HttpServletResponse response) {
    if (!"compact".equals(mode) && !"detailed".equals(mode)) {
      return ResponseEntity.badRequest().body(Map.of("error", "mode must be compact or detailed"));
    }
    ResponseCookie cookie = ResponseCookie.from(VIEW_COOKIE, mode)
        .path("/")
        .httpOnly(true)
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    return ResponseEntity.ok(Map.of("mode", mode));
  }
}
