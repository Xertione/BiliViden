package com.jodio.biliagent.analysis.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("video_analysis_result")
public class VideoAnalysisResultEntity {

    private Long id;

    @TableField("task_id")
    private Long taskId;

    private String summary;

    @TableField("core_points_json")
    private String corePointsJson;

    @TableField("keywords_json")
    private String keywordsJson;

    @TableField("controversies_json")
    private String controversiesJson;

    @TableField("attitude_suggestion")
    private String attitudeSuggestion;

    @TableField("source_basis_json")
    private String sourceBasisJson;

    @TableField("raw_response")
    private String rawResponse;

    @TableField("parsed_result")
    private String parsedResult;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCorePointsJson() {
        return corePointsJson;
    }

    public void setCorePointsJson(String corePointsJson) {
        this.corePointsJson = corePointsJson;
    }

    public String getKeywordsJson() {
        return keywordsJson;
    }

    public void setKeywordsJson(String keywordsJson) {
        this.keywordsJson = keywordsJson;
    }

    public String getControversiesJson() {
        return controversiesJson;
    }

    public void setControversiesJson(String controversiesJson) {
        this.controversiesJson = controversiesJson;
    }

    public String getAttitudeSuggestion() {
        return attitudeSuggestion;
    }

    public void setAttitudeSuggestion(String attitudeSuggestion) {
        this.attitudeSuggestion = attitudeSuggestion;
    }

    public String getSourceBasisJson() {
        return sourceBasisJson;
    }

    public void setSourceBasisJson(String sourceBasisJson) {
        this.sourceBasisJson = sourceBasisJson;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public String getParsedResult() {
        return parsedResult;
    }

    public void setParsedResult(String parsedResult) {
        this.parsedResult = parsedResult;
    }
}
