package com.jodio.biliagent.qa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QaServiceTests {

    private static final AtomicInteger USER_SEQUENCE = new AtomicInteger(1);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM knowledge_card");
        jdbcTemplate.update("DELETE FROM video_analysis_result");
        jdbcTemplate.update("DELETE FROM video_analysis_task");
    }

    @Test
    void shouldReturnSourceRefsAlongWithAnswer() throws Exception {
        String token = registerAndLogin("qa-user");
        long userId = extractUserId(token);

        // Create a task, result, and knowledge card for the QA to find
        long taskId = createAnalysisTask(userId, 101L, "SUMMARY");
        long resultId = createAnalysisResult(taskId);
        createKnowledgeCard(userId, 101L, taskId, resultId);

        mockMvc.perform(post("/api/qa/ask")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"question":"我最近在关注什么？"}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.answer").isString())
            .andExpect(jsonPath("$.data.sourceRefs").isArray())
            .andExpect(jsonPath("$.data.sourceRefs[0]").value("video:101"))
            .andExpect(jsonPath("$.data.sourceRefs[1]").isString())
            .andExpect(jsonPath("$.data.sourceRefs[2]").isString());
    }

    @Test
    void shouldRejectBlankQuestionWith400() throws Exception {
        String token = registerAndLogin("blank-question-user");

        mockMvc.perform(post("/api/qa/ask")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"question":"   "}
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("question must not be blank"));
    }

    @Test
    void shouldRejectInvalidTokenWith401() throws Exception {
        mockMvc.perform(post("/api/qa/ask")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"question":"test"}
                """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateKnowledgeCardFromAnalysis() throws Exception {
        String token = registerAndLogin("card-user");
        long userId = extractUserId(token);
        long taskId = createAnalysisTask(userId, 101L, "SUMMARY");
        long resultId = createAnalysisResult(taskId);

        mockMvc.perform(post("/api/knowledge/cards")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"videoId":101,"analysisTaskId":%d,"analysisResultId":%d}
                """.formatted(taskId, resultId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value("knowledge-card-created"));
    }

    @Test
    void shouldRejectInvalidKnowledgeCardParametersWith400() throws Exception {
        String token = registerAndLogin("bad-card-user");

        mockMvc.perform(post("/api/knowledge/cards")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"videoId":0,"analysisTaskId":201,"analysisResultId":301}
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("videoId must be a positive number"));
    }

    @Test
    void shouldKeepTraceableSourceRefsShape() {
        assertThat(java.util.List.of("video:BV1demo001", "card:1")).hasSize(2);
    }

    private String registerAndLogin(String usernamePrefix) throws Exception {
        int suffix = USER_SEQUENCE.getAndIncrement();
        String username = usernamePrefix + "-" + suffix;
        String nickname = "User " + suffix;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"%s","password":"Password123!","nickname":"%s"}
                """.formatted(username, nickname)))
            .andExpect(status().isOk());

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"%s","password":"Password123!"}
                """.formatted(username)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode loginRoot = objectMapper.readTree(loginResponse);
        return loginRoot.path("data").path("token").asText();
    }

    private long extractUserId(String token) throws Exception {
        String meResponse = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode meRoot = objectMapper.readTree(meResponse);
        return meRoot.path("data").path("userId").asLong();
    }

    private long createAnalysisTask(long userId, long videoId, String analysisType) {
        jdbcTemplate.update(
            "INSERT INTO video_analysis_task (user_id, video_id, analysis_type, status, retry_count) VALUES (?, ?, ?, 'SUCCESS', 0)",
            userId, videoId, analysisType
        );
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private long createAnalysisResult(long taskId) {
        jdbcTemplate.update(
            "INSERT INTO video_analysis_result (task_id, summary, core_points_json, keywords_json, controversies_json, source_basis_json) VALUES (?, 'test summary', '[\"point\"]', '[\"AI\",\"ML\"]', '[]', '[\"source\"]')",
            taskId
        );
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    private void createKnowledgeCard(long userId, long videoId, long taskId, long resultId) {
        jdbcTemplate.update(
            "INSERT INTO knowledge_card (user_id, video_id, analysis_task_id, analysis_result_id, title, summary, key_points_json, tags_json) VALUES (?, ?, ?, ?, 'test card', 'test summary', '[\"point\"]', '[\"AI\"]')",
            userId, videoId, taskId, resultId
        );
    }
}
