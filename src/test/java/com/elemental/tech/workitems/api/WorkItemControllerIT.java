package com.elemental.tech.workitems.api;

import com.elemental.tech.App;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.elemental.tech.workitems.api.dto.CreateWorkItemRequest;
import com.elemental.tech.workitems.api.dto.WorkItemResponse;
import com.elemental.tech.workitems.service.WorkItemService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkItemController.class)
@AutoConfigureMockMvc
@Import(WorkItemControllerTest.TestConfig.class)
@ContextConfiguration(classes = App.class)
class WorkItemControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WorkItemService service;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public WorkItemService workItemService() {
            return Mockito.mock(WorkItemService.class);
        }
    }

    // ---------------------------
    // CREATE
    // ---------------------------

    @Test
    void create_shouldReturnCreated() throws Exception {

        var request = new CreateWorkItemRequest(
                "Hello",
                "World",
                CreateWorkItemRequest.Priority.MEDIUM
        );

        var now = Instant.now();

        var response = new WorkItemResponse(
                1L,
                "Hello",
                "World",
                "OPEN",
                "MEDIUM",
                now,
                now
        );

        given(service.create(any(CreateWorkItemRequest.class)))
                .willReturn(response);

        mvc.perform(post("/api/work-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Hello"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void create_shouldReturnBadRequest_whenInvalid() throws Exception {

        var request = new CreateWorkItemRequest(
                null,
                "World",
                CreateWorkItemRequest.Priority.MEDIUM
        );

        mvc.perform(post("/api/work-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ---------------------------
    // LIST
    // ---------------------------

    @Test
    void list_shouldReturnOk() throws Exception {

        var now = Instant.now();

        given(service.list()).willReturn(List.of(
                new WorkItemResponse(
                        1L, "Task1", "Desc1",
                        "OPEN", "LOW",
                        now, now
                )
        ));

        mvc.perform(get("/api/work-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Task1"));
    }

    // ---------------------------
    // GET
    // ---------------------------

    @Test
    void get_shouldReturnWorkItem_whenExists() throws Exception {

        var now = Instant.now();

        given(service.get(1L)).willReturn(
                new WorkItemResponse(
                        1L, "Task1", "Desc1",
                        "OPEN", "LOW",
                        now, now
                )
        );

        mvc.perform(get("/api/work-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void get_shouldReturnNotFound() throws Exception {

        given(service.get(99L))
                .willThrow(new IllegalArgumentException("work item not found: 99"));

        mvc.perform(get("/api/work-items/99"))
                .andExpect(status().isNotFound());
    }

    // ---------------------------
    // UPDATE STATUS
    // ---------------------------

    @Test
    void updateStatus_shouldReturnOk() throws Exception {

        var now = Instant.now();

        given(service.updateStatus(1L, "DONE", 0L))
                .willReturn(new WorkItemResponse(
                        1L, "Task1", "Desc1",
                        "DONE", "LOW",
                        now, now
                ));

        mvc.perform(patch("/api/work-items/1/status")
                        .param("status", "DONE")
                        .param("version", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void updateStatus_shouldReturnConflict() throws Exception {

        given(service.updateStatus(1L, "DONE", 0L))
                .willThrow(new IllegalStateException("Version mismatch"));

        mvc.perform(patch("/api/work-items/1/status")
                        .param("status", "DONE")
                        .param("version", "0"))
                .andExpect(status().isConflict());
    }
}