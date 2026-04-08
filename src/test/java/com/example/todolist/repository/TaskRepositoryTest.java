package com.example.todolist.repository;

import com.example.todolist.config.JpaConfig;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private TaskAttachmentRepository taskAttachmentRepository;

  @BeforeEach
  void cleanDatabase() {
    taskAttachmentRepository.deleteAll();
    taskRepository.deleteAll();
  }

  @Test
  void saveTaskWithAttachment_persistsRelation() {
    Task task = createTask("Task with attachment", Priority.MEDIUM, false, LocalDate.now().plusDays(3));

    TaskAttachment attachment = new TaskAttachment();
    attachment.setFileName("notes.txt");
    attachment.setStoredFileName("stored-notes.txt");
    attachment.setContentType("text/plain");
    attachment.setSize(12L);
    attachment.setUploadedAt(LocalDateTime.now());
    task.addAttachment(attachment);

    Task saved = taskRepository.saveAndFlush(task);

    List<TaskAttachment> attachments = taskAttachmentRepository.findByTask_IdOrderByIdAsc(saved.getId());

    assertThat(saved.getId()).isNotNull();
    assertThat(attachments).hasSize(1);
    assertThat(attachments.get(0).getFileName()).isEqualTo("notes.txt");
  }

  @Test
  void customQueries_returnExpectedTasks() {
    Task dueSoon = createTask("Due soon", Priority.HIGH, false, LocalDate.now().plusDays(2));
    Task dueLater = createTask("Due later", Priority.HIGH, false, LocalDate.now().plusDays(10));
    Task completed = createTask("Completed", Priority.HIGH, true, LocalDate.now().plusDays(1));

    TaskAttachment attachment = new TaskAttachment();
    attachment.setFileName("report.pdf");
    attachment.setStoredFileName("report-stored.pdf");
    attachment.setContentType("application/pdf");
    attachment.setSize(100L);
    attachment.setUploadedAt(LocalDateTime.now());
    dueSoon.addAttachment(attachment);

    taskRepository.saveAllAndFlush(List.of(dueSoon, dueLater, completed));

    List<Task> byCompletedAndPriority = taskRepository.findByCompletedAndPriority(false, Priority.HIGH);
    List<Task> dueBetween = taskRepository.findTasksDueInNextSevenDays(
        LocalDate.now(),
        LocalDate.now().plusDays(7)
    );
    List<Task> withAttachments = taskRepository.findAllWithAttachments();

    assertThat(byCompletedAndPriority)
        .extracting(Task::getTitle)
        .containsExactlyInAnyOrder("Due soon", "Due later");
    assertThat(dueBetween)
        .extracting(Task::getTitle)
        .containsExactlyInAnyOrder("Due soon", "Completed");
    assertThat(withAttachments)
        .filteredOn(task -> "Due soon".equals(task.getTitle()))
        .singleElement()
        .satisfies(task -> assertThat(task.getAttachments()).hasSize(1));
  }

  private Task createTask(String title, Priority priority, boolean completed, LocalDate dueDate) {
    Task task = new Task();
    task.setTitle(title);
    task.setDescription("Description for " + title);
    task.setCompleted(completed);
    task.setPriority(priority);
    task.setDueDate(dueDate);
    task.setTags(Set.of("study"));
    return task;
  }
}
