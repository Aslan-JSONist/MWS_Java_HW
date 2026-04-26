package com.example.todolist.api;

import com.example.todolist.dto.LoginRequest;
import com.example.todolist.dto.TokenResponse;
import com.example.todolist.dto.ErrorResponse;
import com.example.todolist.security.JwtUtils;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;

  public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
      Authentication auth = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.username(), request.password()));
      String token = jwtUtils.generateToken((UserDetails) auth.getPrincipal());
      return ResponseEntity.ok(new TokenResponse(token));
    } catch (BadCredentialsException ex) {
      ErrorResponse body = new ErrorResponse(
          Instant.now(),
          HttpStatus.UNAUTHORIZED.value(),
          HttpStatus.UNAUTHORIZED.getReasonPhrase(),
          "Invalid credentials",
          "/api/v1/auth/login",
          null
      );
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
  }
}
