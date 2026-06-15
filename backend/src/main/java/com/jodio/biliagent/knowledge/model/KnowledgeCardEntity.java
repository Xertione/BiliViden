package com.jodio.biliagent.knowledge.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jodio.biliagent.common.model.BaseEntity;

@TableName("knowledge_card")
public class KnowledgeCardEntity extends BaseEntity {

    @TableField("video_id")
    private Long videoId;

    @TableField("analysis_task_id")
    private Long analysisTaskId;

    @TableField("analysis_result_id")
    private Long analysisResultId;

    private String title;

    private String summary;

    @TableField("key_points_json")
    private String keyPointsJson;

    @TableField("tags_json")
    private String tagsJson;

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getAnalysisTaskId() {
        return analysisTaskId;
    }

    public void setAnalysisTaskId(Long analysisTaskId) {
        this.analysisTaskId = analysisTaskId;
    }

    public Long getAnalysisResultId() {
        return analysisResultId;
    }

    public void setAnalysisResultId(Long analysisResultId) {
        this.analysisResultId = analysisResultId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getKeyPointsJson() {
        return keyPointsJson;
    }

    public void setKeyPointsJson(String keyPointsJson) {
        this.keyPointsJson = keyPointsJson;
    }

    public String getTagsJson() {
        return tagsJson;
    }

    public void setTagsJson(String tagsJson) {
        this.tagsJson = tagsJson;
    }
}
