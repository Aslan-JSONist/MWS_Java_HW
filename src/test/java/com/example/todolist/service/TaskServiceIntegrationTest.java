package com.example.todolist.service;

import com.example.todolist.dto.TaskPriorityCountDto;
import com.example.todolist.exception.BulkTaskCompletionException;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.repository.TaskAttachmentRepository;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "app.upload-dir=${java.io.tmpdir}/todolist-test-uploads")
class TaskServiceIntegrationTest {

  @Autowired
  private TaskService taskService;

  @Autowired
  private AttachmentService attachmentService;

  @Autowired
  private TaskStatisticsJdbcService taskStatisticsJdbcService;

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
  void bulkCompleteTasks_rollsBackWhenTaskIsMissing() {
    Task first = taskRepository.save(createTask("First", Priority.LOW));
    Task second = taskRepository.save(createTask("Second", Priority.HIGH));

    assertThatThrownBy(() -> taskService.bulkCompleteTasks(List.of(first.getId(), 999999L, second.getId())))
        .isInstanceOf(BulkTaskCompletionException.class);

    assertThat(taskRepository.findById(first.getId())).get().extracting(Task::isCompleted).isEqualTo(false);
    assertThat(taskRepository.findById(second.getId())).get().extracting(Task::isCompleted).isEqualTo(false);
  }

  @Test
  void getAllWithAttachments_returnsTasksWithLoadedAttachments() {
    Task task = taskRepository.save(createTask("Task with file", Priority.MEDIUM));

    TaskAttachment attachment = new TaskAttachment();
    attachment.setFileName("file.txt");
    attachment.setStoredFileName("stored-file.txt");
    attachment.setContentType("text/plain");
    attachment.setSize(5L);
    attachment.setUploadedAt(LocalDateTime.now());
    attachment.setTask(task);
    taskAttachmentRepository.save(attachment);

    List<Task> tasks = taskService.getAllWithAttachments();

    assertThat(tasks)
        .filteredOn(savedTask -> savedTask.getId().equals(task.getId()))
        .singleElement()
        .satisfies(savedTask -> assertThat(savedTask.getAttachments()).hasSize(1));
  }

  @Test
  void attachmentService_storesMetadataInDatabase() {
    Task task = taskRepository.save(createTask("Upload task", Priority.MEDIUM));

    MockMultipartFile file = new MockMultipartFile(
        "file",
        "hello.txt",
        "text/plain",
        "hello".getBytes()
    );

    var savedAttachment = attachmentService.storeAttachment(task.getId(), file);

    assertThat(savedAttachment.getId()).isNotNull();
    assertThat(taskAttachmentRepository.findById(savedAttachment.getId())).isPresent();
  }

  @Test
  void taskStatisticsJdbcService_returnsCountsGroupedByPriority() {
    taskRepository.save(createTask("Low 1", Priority.LOW));
    taskRepository.save(createTask("Low 2", Priority.LOW));
    taskRepository.save(createTask("High 1", Priority.HIGH));

    List<TaskPriorityCountDto> counts = taskStatisticsJdbcService.getTasksCountByPriority();

    assertThat(counts)
        .extracting(TaskPriorityCountDto::getPriority, TaskPriorityCountDto::getCount)
        .contains(org.assertj.core.groups.Tuple.tuple(Priority.LOW, 2L))
        .contains(org.assertj.core.groups.Tuple.tuple(Priority.HIGH, 1L));
  }

  private Task createTask(String title, Priority priority) {
    Task task = new Task();
    task.setTitle(title);
    task.setDescription("Description for " + title);
    task.setCompleted(false);
    task.setPriority(priority);
    task.setDueDate(LocalDate.now().plusDays(2));
    task.setTags(Set.of("integration"));
    return task;
  }
}
