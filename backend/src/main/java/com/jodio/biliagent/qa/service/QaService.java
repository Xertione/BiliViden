package com.jodio.biliagent.qa.service;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class QaService {

    public QaAnswer answer(Long userId, String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("question must not be blank");
        }
        return new QaAnswer(
            "根据你最近沉淀的知识卡片，你更关注 AI 工具的实际落地。",
            List.of("video:BV1demo001", "card:1", "analysis-task:1")
        );
    }

    public record QaAnswer(String answer, List<String> sourceRefs) {
    }
}
