package com.example.todolist.service;

import com.example.todolist.dto.TaskPriorityCountDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Small facade around JDBC task statistics.
 */
@Service
public class TaskStatisticsService {

  private final TaskStatisticsJdbcService taskStatisticsJdbcService;

  public TaskStatisticsService(TaskStatisticsJdbcService taskStatisticsJdbcService) {
    this.taskStatisticsJdbcService = taskStatisticsJdbcService;
  }

  public List<TaskPriorityCountDto> getTasksCountByPriority() {
    return taskStatisticsJdbcService.getTasksCountByPriority();
  }
}