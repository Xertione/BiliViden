package com.jodio.biliagent.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnalysisTaskServiceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    void shouldUsePendingAsInitialStatus() throws Exception {
        Class<?> statusClass = Class.forName("com.jodio.biliagent.analysis.domain.AnalysisStatus");
        @SuppressWarnings("unchecked")
        Class<? extends Enum> enumClass = (Class<? extends Enum>) statusClass;

        assertThat(Enum.valueOf(enumClass, "PENDING").name()).isEqualTo("PENDING");
    }

    @Test
    void shouldCreateAnalysisTaskWithPendingStatus() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"analysis-user","password":"Password123!","nickname":"Analysis User"}
                """))
            .andExpect(status().isOk());

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"analysis-user","password":"Password123!"}
                """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode loginRoot = objectMapper.readTree(loginResponse);
        String token = loginRoot.path("data").path("token").asText();

        mockMvc.perform(post("/api/analysis/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"videoId":101,"analysisType":"SUMMARY"}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value("PENDING"));
    }

    @Test
    void shouldContainRedisDedupeLuaScript() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:lua/analysis_task_dedupe.lua");
        assertThat(resource.exists()).isTrue();

        String script = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertThat(script).contains("redis.call(\"exists\", key)");
        assertThat(script).contains("redis.call(\"set\", key, \"1\", \"EX\", ttlSeconds)");
        assertThat(script).contains("return 0");
        assertThat(script).contains("return 1");
    }
}
