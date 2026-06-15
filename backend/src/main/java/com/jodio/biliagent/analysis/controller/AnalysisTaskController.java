package com.jodio.biliagent.analysis.controller;

import com.jodio.biliagent.analysis.mapper.VideoAnalysisTaskMapper;
import com.jodio.biliagent.analysis.model.VideoAnalysisTaskEntity;
import com.jodio.biliagent.analysis.service.AnalysisTaskService;
import com.jodio.biliagent.auth.service.AuthService;
import com.jodio.biliagent.auth.security.JwtAuthenticationFilter;
import com.jodio.biliagent.common.model.ApiResponse;
import java.util.List;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis/tasks")
public class AnalysisTaskController {

    private final AnalysisTaskService analysisTaskService;
    private final VideoAnalysisTaskMapper taskMapper;
    private final AuthService authService;

    public AnalysisTaskController(
        AnalysisTaskService analysisTaskService,
        VideoAnalysisTaskMapper taskMapper,
        AuthService authService
    ) {
        this.analysisTaskService = analysisTaskService;
        this.taskMapper = taskMapper;
        this.authService = authService;
    }

    @PostMapping
    public ApiResponse<String> create(Authentication authentication, @RequestBody CreateTaskRequest request) {
        Long userId = currentUserId(authentication);
        analysisTaskService.createTask(userId, request.videoId(), request.analysisType());
        return ApiResponse.ok(analysisTaskService.initialStatus().name());
    }

    @GetMapping("/{taskId}")
    public ApiResponse<VideoAnalysisTaskEntity> getTask(Authentication authentication, @PathVariable Long taskId) {
        Long userId = currentUserId(authentication);
        VideoAnalysisTaskEntity task = taskMapper.selectById(taskId);
        if (task == null) {
            return ApiResponse.fail("task not found");
        }
        if (!task.getUserId().equals(userId)) {
            return ApiResponse.fail("task not found");
        }
        return ApiResponse.ok(task);
    }

    @GetMapping
    public ApiResponse<List<VideoAnalysisTaskEntity>> listTasks(Authentication authentication) {
        Long userId = currentUserId(authentication);
        List<VideoAnalysisTaskEntity> tasks = taskMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<VideoAnalysisTaskEntity>()
                .eq(VideoAnalysisTaskEntity::getUserId, userId)
                .orderByDesc(VideoAnalysisTaskEntity::getCreatedAt)
        );
        return ApiResponse.ok(tasks);
    }

    private Long currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtAuthenticationFilter.UserPrincipal userPrincipal) {
            return userPrincipal.userId();
        }
        AuthService.CurrentUserView currentUser = authService.currentUser(authentication.getName());
        if (currentUser.userId() == null) {
            throw new BadCredentialsException("invalid authentication principal");
        }
        return currentUser.userId();
    }

    public record CreateTaskRequest(Long videoId, String analysisType) {
    }
}
