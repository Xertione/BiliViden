package com.jodio.biliagent.analysis.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jodio.biliagent.common.model.BaseEntity;

@TableName("video_analysis_task")
public class VideoAnalysisTaskEntity extends BaseEntity {
    @TableField("video_id")
    private Long videoId;
    @TableField("analysis_type")
    private String analysisType;
    private String status;
    @TableField("retry_count")
    private Integer retryCount;
    @TableField("error_message")
    private String errorMessage;
    @TableField("model_name")
    private String modelName;
    @TableField("prompt_version")
    private String promptVersion;

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }
}
