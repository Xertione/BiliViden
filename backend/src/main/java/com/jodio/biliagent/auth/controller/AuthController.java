package com.jodio.biliagent.auth.controller;

import com.jodio.biliagent.auth.service.AuthService;
import com.jodio.biliagent.auth.security.JwtAuthenticationFilter;
import com.jodio.biliagent.common.model.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {
        authService.register(request.username(), request.password(), request.nickname());
        return ApiResponse.ok("registered");
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.username(), request.password());
        return ApiResponse.ok(new LoginResponse(token));
    }

    @GetMapping("/me")
    public ApiResponse<AuthService.CurrentUserView> me(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtAuthenticationFilter.UserPrincipal userPrincipal) {
            return ApiResponse.ok(authService.currentUser(userPrincipal.username()));
        }
        return ApiResponse.ok(authService.currentUser(authentication.getName()));
    }

    public record RegisterRequest(String username, String password, String nickname) {
    }

    public record LoginRequest(String username, String password) {
    }

    public record LoginResponse(String token) {
    }
}
