package com.jodio.biliagent.bili;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BiliVideoSyncServiceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldBindBiliAccountAndSyncVideoAssets() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"bili-user","password":"Password123!","nickname":"Bili User"}
                """))
            .andExpect(status().isOk());

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"bili-user","password":"Password123!"}
                """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode loginRoot = objectMapper.readTree(loginResponse);
        String token = loginRoot.path("data").path("token").asText();

        mockMvc.perform(post("/api/bili/bind")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"biliUid":"22334455","cookieSnapshot":"SESSDATA=demo-cookie"}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.userId").isNumber())
            .andExpect(jsonPath("$.data.biliUid").value("22334455"))
            .andExpect(jsonPath("$.data.bindStatus").value("BOUND"));

        mockMvc.perform(post("/api/bili/sync")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.biliUid").value("22334455"))
            .andExpect(jsonPath("$.data.counts.historyCount").value(2))
            .andExpect(jsonPath("$.data.counts.favoritesCount").value(1))
            .andExpect(jsonPath("$.data.counts.watchLaterCount").value(1))
            .andExpect(jsonPath("$.data.counts.uniqueVideoCount").value(3))
            .andExpect(jsonPath("$.data.counts.sourceRecordCount").value(4))
            .andExpect(jsonPath("$.data.syncedAt").isString());
    }

    @Test
    void shouldRejectSyncBeforeBinding() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"unbound-user","password":"Password123!","nickname":"Unbound User"}
                """))
            .andExpect(status().isOk());

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"unbound-user","password":"Password123!"}
                """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode loginRoot = objectMapper.readTree(loginResponse);
        String token = loginRoot.path("data").path("token").asText();

        mockMvc.perform(post("/api/bili/sync")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("bili account not bound"));
    }
}
