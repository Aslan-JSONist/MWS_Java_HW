package com.example.todolist.repository;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * JPA repository for tasks.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

  @Override
  @EntityGraph(attributePaths = "tags")
  java.util.Optional<Task> findById(Long id);

  @EntityGraph(attributePaths = "tags")
  List<Task> findAllByOrderByIdAsc();

  @Query("select t from Task t where t.dueDate between :today and :nextSevenDays order by t.dueDate asc")
  List<Task> findTasksDueInNextSevenDays(@Param("today") LocalDate today,
      @Param("nextSevenDays") LocalDate nextSevenDays);

  @EntityGraph(attributePaths = {"attachments", "tags"})
  @Query("select t from Task t order by t.id asc")
  List<Task> findAllWithAttachments();
}
