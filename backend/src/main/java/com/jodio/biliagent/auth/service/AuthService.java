package com.jodio.biliagent.auth.service;

import com.jodio.biliagent.auth.mapper.UserMapper;
import com.jodio.biliagent.auth.model.UserEntity;
import com.jodio.biliagent.auth.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserEntity register(String username, String rawPassword, String nickname) {
        if (userMapper.findByUsername(username) != null) {
            throw new IllegalArgumentException("username already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setNickname(nickname);
        user.setStatus("ACTIVE");
        userMapper.insert(user);
        return user;
    }

    public String login(String username, String rawPassword) {
        UserEntity user = userMapper.findByUsername(username);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return jwtService.generateToken(user.getId(), user.getUsername());
    }

    public CurrentUserView currentUser(String username) {
        UserEntity user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BadCredentialsException("User not found");
        }
        return new CurrentUserView(user.getId(), user.getUsername(), user.getNickname(), user.getStatus());
    }

    public record CurrentUserView(Long userId, String username, String nickname, String status) {
    }
}
