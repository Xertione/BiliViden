package com.jodio.biliagent.analysis.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jodio.biliagent.analysis.model.VideoAnalysisTaskEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface VideoAnalysisTaskMapper extends BaseMapper<VideoAnalysisTaskEntity> {

    default List<VideoAnalysisTaskEntity> findPendingTasks(int limit) {
        return selectList(new LambdaQueryWrapper<VideoAnalysisTaskEntity>()
            .eq(VideoAnalysisTaskEntity::getStatus, "PENDING")
            .orderByAsc(VideoAnalysisTaskEntity::getCreatedAt)
            .last("limit " + limit));
    }

    default List<VideoAnalysisTaskEntity> findRetryableTasks(int maxRetries, int limit) {
        return selectList(new LambdaQueryWrapper<VideoAnalysisTaskEntity>()
            .eq(VideoAnalysisTaskEntity::getStatus, "FAILED")
            .lt(VideoAnalysisTaskEntity::getRetryCount, maxRetries)
            .orderByAsc(VideoAnalysisTaskEntity::getCreatedAt)
            .last("limit " + limit));
    }

    @Update("UPDATE video_analysis_task SET status = 'RUNNING', started_at = NOW() WHERE id = #{taskId} AND status = 'PENDING'")
    int claimPendingTask(Long taskId);

    @Update("UPDATE video_analysis_task SET status = 'RUNNING', retry_count = retry_count + 1, started_at = NOW() WHERE id = #{taskId} AND status = 'FAILED' AND retry_count < #{maxRetries}")
    int claimRetryTask(Long taskId, int maxRetries);
}
