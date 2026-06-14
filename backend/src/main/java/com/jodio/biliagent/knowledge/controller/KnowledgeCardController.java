package com.jodio.biliagent.knowledge.controller;

import com.jodio.biliagent.auth.service.AuthService;
import com.jodio.biliagent.auth.security.JwtAuthenticationFilter;
import com.jodio.biliagent.common.model.ApiResponse;
import com.jodio.biliagent.knowledge.service.KnowledgeCardService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge/cards")
public class KnowledgeCardController {

    private final KnowledgeCardService knowledgeCardService;
    private final AuthService authService;

    public KnowledgeCardController(KnowledgeCardService knowledgeCardService, AuthService authService) {
        this.knowledgeCardService = knowledgeCardService;
        this.authService = authService;
    }

    @PostMapping
    public ApiResponse<String> create(Authentication authentication, @RequestBody CreateCardRequest request) {
        return ApiResponse.ok(
            knowledgeCardService.createFromAnalysis(
                currentUserId(authentication),
                request.videoId(),
                request.analysisTaskId(),
                request.analysisResultId()
            )
        );
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

    public record CreateCardRequest(Long videoId, Long analysisTaskId, Long analysisResultId) {
    }
}
