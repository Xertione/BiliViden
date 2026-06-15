package com.jodio.biliagent.knowledge.service;

import com.jodio.biliagent.analysis.mapper.VideoAnalysisResultMapper;
import com.jodio.biliagent.analysis.mapper.VideoAnalysisTaskMapper;
import com.jodio.biliagent.analysis.model.VideoAnalysisResultEntity;
import com.jodio.biliagent.analysis.model.VideoAnalysisTaskEntity;
import com.jodio.biliagent.knowledge.mapper.KnowledgeCardMapper;
import com.jodio.biliagent.knowledge.model.KnowledgeCardEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KnowledgeCardService {

    private final KnowledgeCardMapper cardMapper;
    private final VideoAnalysisTaskMapper taskMapper;
    private final VideoAnalysisResultMapper resultMapper;

    public KnowledgeCardService(
        KnowledgeCardMapper cardMapper,
        VideoAnalysisTaskMapper taskMapper,
        VideoAnalysisResultMapper resultMapper
    ) {
        this.cardMapper = cardMapper;
        this.taskMapper = taskMapper;
        this.resultMapper = resultMapper;
    }

    @Transactional
    public String createFromAnalysis(Long userId, Long videoId, Long taskId, Long resultId) {
        requirePositive(userId, "userId");
        requirePositive(videoId, "videoId");
        requirePositive(taskId, "analysisTaskId");
        requirePositive(resultId, "analysisResultId");

        // Verify the analysis task exists and belongs to this user
        VideoAnalysisTaskEntity task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("analysis task not found: " + taskId);
        }
        if (!task.getUserId().equals(userId)) {
            throw new IllegalArgumentException("analysis task does not belong to current user");
        }

        // Verify the analysis result exists
        VideoAnalysisResultEntity result = resultMapper.selectById(resultId);
        if (result == null) {
            throw new IllegalArgumentException("analysis result not found: " + resultId);
        }

        // Build knowledge card from analysis result
        KnowledgeCardEntity card = new KnowledgeCardEntity();
        card.setUserId(userId);
        card.setVideoId(videoId);
        card.setAnalysisTaskId(taskId);
        card.setAnalysisResultId(resultId);
        card.setTitle(result.getKeywordsJson() != null ? extractFirstKeyword(result.getKeywordsJson()) : "知识卡片");
        card.setSummary(result.getSummary());
        card.setKeyPointsJson(result.getCorePointsJson());
        card.setTagsJson(result.getKeywordsJson());
        cardMapper.insert(card);

        return "knowledge-card-created";
    }

    public KnowledgeCardEntity findById(Long cardId) {
        return cardMapper.selectById(cardId);
    }

    private String extractFirstKeyword(String keywordsJson) {
        // Simple extraction: "["AI","ML"]" -> "AI"
        if (keywordsJson == null || keywordsJson.isBlank()) {
            return "知识卡片";
        }
        String trimmed = keywordsJson.trim();
        if (trimmed.startsWith("[\"") && trimmed.length() > 3) {
            int endQuote = trimmed.indexOf("\"", 2);
            if (endQuote > 2) {
                return trimmed.substring(2, endQuote);
            }
        }
        return "知识卡片";
    }

    private void requirePositive(Long value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be a positive number");
        }
    }
}
