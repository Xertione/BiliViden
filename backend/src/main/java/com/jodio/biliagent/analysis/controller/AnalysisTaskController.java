package com.jodio.biliagent.analysis.controller;

import com.jodio.biliagent.analysis.service.AnalysisTaskService;
import com.jodio.biliagent.common.model.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis/tasks")
public class AnalysisTaskController {

    private final AnalysisTaskService analysisTaskService;

    public AnalysisTaskController(AnalysisTaskService analysisTaskService) {
        this.analysisTaskService = analysisTaskService;
    }

    @PostMapping
    public ApiResponse<String> create(@RequestBody CreateTaskRequest request) {
        return ApiResponse.ok(analysisTaskService.initialStatus().name());
    }

    public record CreateTaskRequest(Long videoId, String analysisType) {
    }
}
