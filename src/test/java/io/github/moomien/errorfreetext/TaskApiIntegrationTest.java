package io.github.moomien.errorfreetext;


import com.jayway.jsonpath.JsonPath;
import io.github.moomien.errorfreetext.correction.SpellError;
import io.github.moomien.errorfreetext.domain.TaskRepository;
import io.github.moomien.errorfreetext.scheduler.TaskProcessingScheduler;
import io.github.moomien.errorfreetext.speller.SpellerClient;
import io.github.moomien.errorfreetext.speller.SpellerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.scheduling.enabled=false")
@AutoConfigureMockMvc
@Testcontainers
class TaskApiIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @MockitoBean
    SpellerClient spellerClient;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TaskProcessingScheduler scheduler;

    @Autowired
    TaskRepository taskRepository;

    @BeforeEach
    void cleanDatabase() {
        taskRepository.deleteAll();
    }

    @Test
    void createTask_returnsIdAndSavesToDatabase() throws Exception {
        String id = createTask("Превет мир", "RU");

        mockMvc.perform(get("/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void scheduledProcessing_correctsTextAndMarksDone() throws Exception {
        when(spellerClient.checkTexts(anyList(), eq("ru"), anyInt()))
                .thenReturn(List.of(List.of(new SpellError(0, 6, List.of("Привет")))));

        String id = createTask("Превет мир", "RU");

        scheduler.processNewTasks();

        mockMvc.perform(get("/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.correctedText").value("Привет мир"));
    }

    @Test
    void spellerFailure_marksTaskAsError() throws Exception {
        when(spellerClient.checkTexts(anyList(), anyString(), anyInt()))
                .thenThrow(new SpellerException("Yandex Speller is unavailable"));

        String id = createTask("Превет мир", "RU");

        scheduler.processNewTasks();

        mockMvc.perform(get("/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("Yandex Speller is unavailable"))
                .andExpect(jsonPath("$.correctedText").doesNotExist());
    }
    @Test
    void getUnknownTask_returns404InCommonFormat() throws Exception{
        mockMvc.perform(get("/tasks/{id}","44bd78dc-d08c-41c6-b87d-fb82046bd470"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(40401))
                .andExpect(jsonPath("$.path").value("/tasks/44bd78dc-d08c-41c6-b87d-fb82046bd470"));
    }

    private String createTask(String text, String language) throws Exception {
        String body = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text": "%s", "language": "%s"}
                                """.formatted(text, language)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }
}

