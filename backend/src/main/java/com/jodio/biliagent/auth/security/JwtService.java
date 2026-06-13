package com.jodio.biliagent.auth.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String generateToken(Long userId, String username) {
        String payload = userId + ":" + username;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }
}
