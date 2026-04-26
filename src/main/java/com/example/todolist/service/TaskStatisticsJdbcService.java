package com.example.todolist.service;

import com.example.todolist.dto.TaskPriorityCountDto;
import com.example.todolist.model.Priority;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reads task statistics with JdbcTemplate.
 */
@Service
public class TaskStatisticsJdbcService {

  private static final String SQL_TASKS_COUNT_BY_PRIORITY = """
      select priority, count(*) as tasks_count
      from tasks
      group by priority
      order by priority
      """;

  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<TaskPriorityCountDto> taskPriorityCountRowMapper = (rs, rowNum) ->
      new TaskPriorityCountDto(
          Priority.valueOf(rs.getString("priority")),
          rs.getLong("tasks_count")
      );

  public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Transactional(readOnly = true)
  public List<TaskPriorityCountDto> getTasksCountByPriority() {
    return jdbcTemplate.query(SQL_TASKS_COUNT_BY_PRIORITY, taskPriorityCountRowMapper);
  }
}
