package com.example.todolist.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

  private final SecretKey key;
  private final long expirationMs;

  public JwtUtils(
      @Value("${app.security.jwt.secret}") String secret,
      @Value("${app.security.jwt.expiration-ms}") long expirationMs) {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < 32) {
      throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 bytes)");
    }
    this.key = Keys.hmacShaKeyFor(keyBytes);
    this.expirationMs = expirationMs;
  }

  public String generateToken(UserDetails user) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expirationMs);
    String authorities = user.getAuthorities().stream()
        .map(a -> a.getAuthority())
        .collect(Collectors.joining(","));
    return Jwts.builder()
        .subject(user.getUsername())
        .claim("authorities", authorities)
        .issuedAt(now)
        .expiration(exp)
        .signWith(key)
        .compact();
  }

  public Authentication parseToken(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
    String username = claims.getSubject();
    String authoritiesClaim = claims.get("authorities", String.class);
    var authorities = Arrays.stream(authoritiesClaim != null ? authoritiesClaim.split(",") : new String[0])
        .filter(s -> !s.isBlank())
        .map(SimpleGrantedAuthority::new)
        .toList();
    UserDetails principal = User.builder()
        .username(username)
        .password("")
        .authorities(authorities)
        .build();
    return new UsernamePasswordAuthenticationToken(principal, null, authorities);
  }
}
