package com.jodio.biliagent.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${app.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String username) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(username)
            .claim("uid", userId)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(86400)))
            .signWith(secretKey)
            .compact();
    }

    public JwtPrincipal parseToken(String token) {
        Jws<Claims> jws = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token);

        Claims payload = jws.getPayload();
        return new JwtPrincipal(
            payload.get("uid", Long.class),
            payload.getSubject()
        );
    }

    public record JwtPrincipal(Long userId, String username) {
    }
}
