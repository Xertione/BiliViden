package com.jodio.biliagent.analysis.config;

import com.jodio.biliagent.analysis.domain.AnalysisStatus;
import com.jodio.biliagent.analysis.dto.VideoAnalysisResultDto;
import com.jodio.biliagent.analysis.mapper.VideoAnalysisResultMapper;
import com.jodio.biliagent.analysis.mapper.VideoAnalysisTaskMapper;
import com.jodio.biliagent.analysis.model.VideoAnalysisResultEntity;
import com.jodio.biliagent.analysis.model.VideoAnalysisTaskEntity;
import com.jodio.biliagent.analysis.service.VideoAnalysisExecutor;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.analysis.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class AnalysisTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(AnalysisTaskScheduler.class);
    private static final int MAX_RETRIES = 3;
    private static final int BATCH_SIZE = 5;

    private final VideoAnalysisTaskMapper taskMapper;
    private final VideoAnalysisResultMapper resultMapper;
    private final VideoAnalysisExecutor executor;

    public AnalysisTaskScheduler(
        VideoAnalysisTaskMapper taskMapper,
        VideoAnalysisResultMapper resultMapper,
        ObjectProvider<VideoAnalysisExecutor> executorProvider
    ) {
        this.taskMapper = taskMapper;
        this.resultMapper = resultMapper;
        this.executor = executorProvider.getIfAvailable();
    }

    @Scheduled(fixedDelay = 15000)
    public void processPendingTasks() {
        List<VideoAnalysisTaskEntity> pending = taskMapper.findPendingTasks(BATCH_SIZE);
        for (VideoAnalysisTaskEntity task : pending) {
            int claimed = taskMapper.claimPendingTask(task.getId());
            if (claimed > 0) {
                executeTask(task.getId());
            }
        }

        List<VideoAnalysisTaskEntity> retryable = taskMapper.findRetryableTasks(MAX_RETRIES, BATCH_SIZE);
        for (VideoAnalysisTaskEntity task : retryable) {
            int claimed = taskMapper.claimRetryTask(task.getId(), MAX_RETRIES);
            if (claimed > 0) {
                executeTask(task.getId());
            }
        }
    }

    private void executeTask(Long taskId) {
        VideoAnalysisTaskEntity task = taskMapper.selectById(taskId);
        if (task == null) {
            return;
        }

        try {
            if (executor == null) {
                failTask(task, "AI service not configured, please set app.ai.openai.api-key");
                return;
            }

            String material = buildMaterial(task);
            VideoAnalysisResultDto result = executor.videoAnalysisAiService().analyze(material);
            executor.validate(result);
            saveResultAndSucceed(task, result);

        } catch (UnsupportedOperationException e) {
            failTask(task, "AI analysis execution is in skeleton stage: " + e.getMessage());
        } catch (Exception e) {
            log.error("Analysis execution failed for task {}: {}", taskId, e.getMessage(), e);
            handleExecutionFailure(task, e);
        }
    }

    @Transactional
    protected void saveResultAndSucceed(VideoAnalysisTaskEntity task, VideoAnalysisResultDto result) {
        VideoAnalysisResultEntity resultEntity = new VideoAnalysisResultEntity();
        resultEntity.setTaskId(task.getId());
        resultEntity.setSummary(result.summary());
        resultEntity.setCorePointsJson(toJsonArray(result.corePoints()));
        resultEntity.setKeywordsJson(toJsonArray(result.keywords()));
        resultEntity.setControversiesJson(toJsonArray(result.controversies()));
        resultEntity.setAttitudeSuggestion(result.attitudeSuggestion());
        resultEntity.setSourceBasisJson(toJsonArray(result.sourceBasis()));
        resultMapper.insert(resultEntity);

        task.setStatus(AnalysisStatus.SUCCESS.name());
        task.setFinishedAt(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    @Transactional
    protected void failTask(VideoAnalysisTaskEntity task, String errorMessage) {
        task.setStatus(AnalysisStatus.FAILED.name());
        task.setErrorMessage(errorMessage);
        task.setFinishedAt(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    private void handleExecutionFailure(VideoAnalysisTaskEntity task, Exception e) {
        int currentRetry = task.getRetryCount() != null ? task.getRetryCount() : 0;
        if (currentRetry >= MAX_RETRIES - 1) {
            task.setStatus(AnalysisStatus.FAILED.name());
            task.setErrorMessage("Max retries exceeded: " + e.getMessage());
        } else {
            task.setStatus(AnalysisStatus.FAILED.name());
            task.setErrorMessage(e.getMessage());
            task.setRetryCount(currentRetry + 1);
        }
        task.setFinishedAt(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    private String buildMaterial(VideoAnalysisTaskEntity task) {
        return "分析视频 ID: " + task.getVideoId()
            + ", 分析类型: " + task.getAnalysisType();
    }

    private String toJsonArray(List<String> items) {
        if (items == null) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(escapeJson(items.get(i))).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
