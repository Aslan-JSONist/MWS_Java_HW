package com.example.todolist.controller;

import com.example.todolist.model.TaskAttachment;
import com.example.todolist.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttachmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AttachmentControllerWebMvcTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AttachmentService attachmentService;

  @Test
  void upload_positive() throws Exception {
    TaskAttachment saved = new TaskAttachment(5L, 1L, "a.txt", "uuid", "text/plain", 3L,
        LocalDateTime.now());
    when(attachmentService.storeAttachment(eq(1L), any())).thenReturn(saved);

    MockMultipartFile file = new MockMultipartFile(
        "file",
        "a.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "abc".getBytes()
    );

    mockMvc.perform(multipart("/api/tasks/1/attachments").file(file))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(5))
        .andExpect(jsonPath("$.fileName").value("a.txt"));

    verify(attachmentService).storeAttachment(eq(1L), any());
  }

  @Test
  void list_positive() throws Exception {
    TaskAttachment a = new TaskAttachment(1L, 2L, "f.txt", "s", "text/plain", 1L, LocalDateTime.now());
    when(attachmentService.listForTask(2L)).thenReturn(List.of(a));

    mockMvc.perform(get("/api/tasks/2/attachments"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1));
  }

  @Test
  void delete_positive() throws Exception {
    mockMvc.perform(delete("/api/attachments/9"))
        .andExpect(status().isNoContent());
    verify(attachmentService).deleteAttachment(9L);
  }
}
