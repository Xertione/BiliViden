package com.jodio.biliagent.knowledge.controller;

import com.jodio.biliagent.auth.service.AuthService;
import com.jodio.biliagent.auth.security.JwtAuthenticationFilter;
import com.jodio.biliagent.common.model.ApiResponse;
import com.jodio.biliagent.knowledge.mapper.KnowledgeCardMapper;
import com.jodio.biliagent.knowledge.model.KnowledgeCardEntity;
import com.jodio.biliagent.knowledge.service.KnowledgeCardService;
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
@RequestMapping("/api/knowledge/cards")
public class KnowledgeCardController {

    private final KnowledgeCardService knowledgeCardService;
    private final KnowledgeCardMapper cardMapper;
    private final AuthService authService;

    public KnowledgeCardController(
        KnowledgeCardService knowledgeCardService,
        KnowledgeCardMapper cardMapper,
        AuthService authService
    ) {
        this.knowledgeCardService = knowledgeCardService;
        this.cardMapper = cardMapper;
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

    @GetMapping("/{cardId}")
    public ApiResponse<KnowledgeCardEntity> getCard(Authentication authentication, @PathVariable Long cardId) {
        Long userId = currentUserId(authentication);
        KnowledgeCardEntity card = cardMapper.selectById(cardId);
        if (card == null) {
            return ApiResponse.fail("card not found");
        }
        if (!card.getUserId().equals(userId)) {
            return ApiResponse.fail("card not found");
        }
        return ApiResponse.ok(card);
    }

    @GetMapping
    public ApiResponse<List<KnowledgeCardEntity>> listCards(Authentication authentication) {
        Long userId = currentUserId(authentication);
        List<KnowledgeCardEntity> cards = cardMapper.findByUserId(userId);
        return ApiResponse.ok(cards);
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
