package com.example.todolist.controller;

import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Favorite tasks stored in the HTTP session.
 */
@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Session-based favorites")
public class FavoritesController {

  private final FavoritesService favoritesService;

  public FavoritesController(FavoritesService favoritesService) {
    this.favoritesService = favoritesService;
  }

  @Operation(summary = "Add task to favorites")
  @PostMapping("/{taskId}")
  public ResponseEntity<Void> add(@PathVariable Long taskId, HttpSession session) {
    favoritesService.addFavorite(session, taskId);
    return ResponseEntity.status(201).build();
  }

  @Operation(summary = "Remove task from favorites")
  @DeleteMapping("/{taskId}")
  public ResponseEntity<Void> remove(@PathVariable Long taskId, HttpSession session) {
    favoritesService.removeFavorite(session, taskId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "List favorite tasks")
  @GetMapping
  public ResponseEntity<List<TaskResponseDto>> list(HttpSession session) {
    return ResponseEntity.ok(favoritesService.listFavorites(session));
  }
}
