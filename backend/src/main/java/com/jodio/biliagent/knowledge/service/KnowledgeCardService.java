package com.jodio.biliagent.knowledge.service;

import org.springframework.stereotype.Service;

@Service
public class KnowledgeCardService {

    public String createFromAnalysis(Long userId, Long videoId, Long taskId, Long resultId) {
        requirePositive(userId, "userId");
        requirePositive(videoId, "videoId");
        requirePositive(taskId, "analysisTaskId");
        requirePositive(resultId, "analysisResultId");
        return "knowledge-card-created";
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
