package io.github.moomien.errorfreetext;


import io.github.moomien.errorfreetext.speller.SpellerClient;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
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

    @Test
    void createTask_returnsIdAndSavesToDatabase() throws Exception {
        String body = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Превет мир\", \"language\": \"RU\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        String id = body.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");
        mockMvc.perform(get("/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void getUnknownTask_returns404InCommonFormat() throws Exception{
        mockMvc.perform(get("/tasks/{id}","44bd78dc-d08c-41c6-b87d-fb82046bd470"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(40401))
                .andExpect(jsonPath("$.path").value("/tasks/44bd78dc-d08c-41c6-b87d-fb82046bd470"));
    }
}
