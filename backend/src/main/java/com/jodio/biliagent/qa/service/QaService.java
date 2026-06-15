package com.jodio.biliagent.qa.service;

import com.jodio.biliagent.analysis.mapper.VideoAnalysisResultMapper;
import com.jodio.biliagent.analysis.mapper.VideoAnalysisTaskMapper;
import com.jodio.biliagent.analysis.model.VideoAnalysisResultEntity;
import com.jodio.biliagent.analysis.model.VideoAnalysisTaskEntity;
import com.jodio.biliagent.knowledge.mapper.KnowledgeCardMapper;
import com.jodio.biliagent.knowledge.model.KnowledgeCardEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class QaService {

    private final KnowledgeCardMapper cardMapper;
    private final VideoAnalysisTaskMapper taskMapper;
    private final VideoAnalysisResultMapper resultMapper;

    public QaService(
        KnowledgeCardMapper cardMapper,
        VideoAnalysisTaskMapper taskMapper,
        VideoAnalysisResultMapper resultMapper
    ) {
        this.cardMapper = cardMapper;
        this.taskMapper = taskMapper;
        this.resultMapper = resultMapper;
    }

    public QaAnswer answer(Long userId, String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("question must not be blank");
        }

        // Retrieve all knowledge cards for this user
        List<KnowledgeCardEntity> cards = cardMapper.findByUserId(userId);
        if (cards.isEmpty()) {
            return new QaAnswer(
                "你还没有沉淀知识卡片。请先同步视频并创建分析任务，再将分析结果沉淀为知识卡片。",
                List.of()
            );
        }

        // Gather source references from all knowledge cards
        List<String> sourceRefs = new ArrayList<>();
        StringBuilder answerContext = new StringBuilder();
        answerContext.append("根据你的知识卡片，以下是相关总结：\n\n");

        for (int i = 0; i < Math.min(cards.size(), 3); i++) {
            KnowledgeCardEntity card = cards.get(i);
            answerContext.append("卡片").append(i + 1).append("：").append(card.getSummary()).append("\n");
            sourceRefs.add("video:" + card.getVideoId());
            sourceRefs.add("card:" + card.getId());
            sourceRefs.add("analysis-task:" + card.getAnalysisTaskId());

            // Also get the analysis result for more detail
            VideoAnalysisResultEntity result = resultMapper.selectById(card.getAnalysisResultId());
            if (result != null) {
                answerContext.append("  核心观点：").append(formatPoints(result.getCorePointsJson())).append("\n");
            }
        }

        return new QaAnswer(answerContext.toString().trim(), sourceRefs);
    }

    private String formatPoints(String pointsJson) {
        if (pointsJson == null || pointsJson.isBlank()) {
            return "";
        }
        return pointsJson
            .replace("[", "")
            .replace("]", "")
            .replace("\"", "")
            .replace(",", "；");
    }

    public record QaAnswer(String answer, List<String> sourceRefs) {
    }
}
