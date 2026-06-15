package com.jodio.biliagent.analysis.service;

import com.jodio.biliagent.analysis.domain.AnalysisStatus;
import com.jodio.biliagent.analysis.mapper.VideoAnalysisTaskMapper;
import com.jodio.biliagent.analysis.model.VideoAnalysisTaskEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalysisTaskService {

    private final VideoAnalysisTaskMapper taskMapper;

    public AnalysisTaskService(VideoAnalysisTaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Transactional
    public VideoAnalysisTaskEntity createTask(Long userId, Long videoId, String analysisType) {
        if (videoId == null || videoId <= 0) {
            throw new IllegalArgumentException("videoId must be a positive number");
        }
        if (analysisType == null || analysisType.isBlank()) {
            throw new IllegalArgumentException("analysisType must not be blank");
        }

        VideoAnalysisTaskEntity task = new VideoAnalysisTaskEntity();
        task.setUserId(userId);
        task.setVideoId(videoId);
        task.setAnalysisType(analysisType);
        task.setStatus(AnalysisStatus.PENDING.name());
        task.setRetryCount(0);
        taskMapper.insert(task);
        return task;
    }

    public AnalysisStatus initialStatus() {
        return AnalysisStatus.PENDING;
    }
}
