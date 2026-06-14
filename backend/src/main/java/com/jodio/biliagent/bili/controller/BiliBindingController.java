package com.jodio.biliagent.bili.controller;

import com.jodio.biliagent.auth.service.AuthService;
import com.jodio.biliagent.auth.security.JwtAuthenticationFilter;
import com.jodio.biliagent.bili.service.BiliBindingService;
import com.jodio.biliagent.bili.service.BiliVideoSyncService;
import com.jodio.biliagent.common.model.ApiResponse;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bili")
public class BiliBindingController {

    private final BiliBindingService biliBindingService;
    private final BiliVideoSyncService biliVideoSyncService;
    private final AuthService authService;

    public BiliBindingController(
        AuthService authService,
        BiliBindingService biliBindingService,
        BiliVideoSyncService biliVideoSyncService
    ) {
        this.authService = authService;
        this.biliBindingService = biliBindingService;
        this.biliVideoSyncService = biliVideoSyncService;
    }

    @PostMapping("/bind")
    public ApiResponse<BiliBindingService.BindingResult> bind(
        Authentication authentication,
        @RequestBody BindRequest request
    ) {
        return ApiResponse.ok(
            biliBindingService.bind(currentUserId(authentication), request.biliUid(), request.cookieSnapshot())
        );
    }

    @PostMapping("/sync")
    public ApiResponse<SyncResponse> sync(Authentication authentication) {
        Long userId = currentUserId(authentication);
        BiliBindingService.BindingRecord binding = biliBindingService.requireBinding(userId);
        BiliVideoSyncService.SyncResult syncResult = biliVideoSyncService.syncAllSources(userId, binding.cookieSnapshot());
        LocalDateTime syncedAt = LocalDateTime.now();
        biliBindingService.markSynced(userId, syncedAt);
        return ApiResponse.ok(new SyncResponse(binding.biliUid(), syncResult.counts(), syncedAt));
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

    public record BindRequest(String biliUid, String cookieSnapshot) {
    }

    public record SyncResponse(String biliUid, Map<String, Integer> counts, LocalDateTime syncedAt) {
    }
}
