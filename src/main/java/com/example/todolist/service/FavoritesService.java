package com.example.todolist.service;

import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Task;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores favorite task ids in the HTTP session.
 */
@Service
public class FavoritesService {

  public static final String SESSION_FAVORITES = "favoriteTaskIds";

  private final TaskService taskService;
  private final TaskMapper taskMapper;

  public FavoritesService(TaskService taskService, TaskMapper taskMapper) {
    this.taskService = taskService;
    this.taskMapper = taskMapper;
  }

  @SuppressWarnings("unchecked")
  private Set<Long> favorites(HttpSession session) {
    Object attr = session.getAttribute(SESSION_FAVORITES);
    if (attr instanceof Set<?> set) {
      return (Set<Long>) set;
    }
    Set<Long> created = new LinkedHashSet<>();
    session.setAttribute(SESSION_FAVORITES, created);
    return created;
  }

  public void addFavorite(HttpSession session, Long taskId) {
    if (taskService.getById(taskId).isEmpty()) {
      throw new TaskNotFoundException(taskId);
    }
    favorites(session).add(taskId);
  }

  public void removeFavorite(HttpSession session, Long taskId) {
    favorites(session).remove(taskId);
  }

  public List<TaskResponseDto> listFavorites(HttpSession session) {
    List<TaskResponseDto> result = new ArrayList<>();
    for (Long id : favorites(session)) {
      Task task = taskService.getById(id).orElse(null);
      if (task != null) {
        result.add(taskMapper.toResponseDto(task));
      }
    }
    return result;
  }
}
