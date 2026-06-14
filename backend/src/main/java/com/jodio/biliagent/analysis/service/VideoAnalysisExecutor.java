package com.jodio.biliagent.analysis.service;

import com.jodio.biliagent.analysis.ai.VideoAnalysisAiService;
import com.jodio.biliagent.analysis.dto.VideoAnalysisResultDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(VideoAnalysisAiService.class)
public class VideoAnalysisExecutor {

    private final VideoAnalysisAiService videoAnalysisAiService;

    public VideoAnalysisExecutor(VideoAnalysisAiService videoAnalysisAiService) {
        this.videoAnalysisAiService = videoAnalysisAiService;
    }

    public void validate(VideoAnalysisResultDto result) {
        if (result.summary() == null || result.summary().isBlank()) {
            throw new IllegalArgumentException("summary must not be blank");
        }
        if (result.corePoints() == null || result.corePoints().isEmpty()) {
            throw new IllegalArgumentException("corePoints must not be empty");
        }
    }

    public VideoAnalysisAiService videoAnalysisAiService() {
        return videoAnalysisAiService;
    }
}
