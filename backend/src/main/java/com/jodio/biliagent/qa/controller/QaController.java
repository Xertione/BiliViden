package com.jodio.biliagent.qa.controller;

import com.jodio.biliagent.auth.service.AuthService;
import com.jodio.biliagent.auth.security.JwtAuthenticationFilter;
import com.jodio.biliagent.common.model.ApiResponse;
import com.jodio.biliagent.qa.service.QaService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qa")
public class QaController {

    private final QaService qaService;
    private final AuthService authService;

    public QaController(QaService qaService, AuthService authService) {
        this.qaService = qaService;
        this.authService = authService;
    }

    @PostMapping("/ask")
    public ApiResponse<QaService.QaAnswer> ask(Authentication authentication, @RequestBody AskRequest request) {
        return ApiResponse.ok(qaService.answer(currentUserId(authentication), request.question()));
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

    public record AskRequest(String question) {
    }
}
