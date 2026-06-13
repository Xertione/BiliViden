package com.jodio.biliagent.auth.controller;

import com.jodio.biliagent.auth.model.UserEntity;
import com.jodio.biliagent.auth.service.AuthService;
import com.jodio.biliagent.common.model.ApiResponse;
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
    public ApiResponse<UserEntity> register(@RequestBody RegisterRequest request) {
        return ApiResponse.ok(
            authService.register(request.username(), request.password(), request.nickname())
        );
    }

    @GetMapping("/me")
    public ApiResponse<String> me() {
        return ApiResponse.ok("me");
    }

    public record RegisterRequest(String username, String password, String nickname) {
    }
}
