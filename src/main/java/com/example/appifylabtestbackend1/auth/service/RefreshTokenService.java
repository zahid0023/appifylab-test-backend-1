package com.example.appifylabtestbackend1.auth.service;

import com.example.appifylabtestbackend1.auth.dto.response.LoginResponse;
import com.example.appifylabtestbackend1.auth.model.enitty.UserEntity;
import org.springframework.security.core.Authentication;

public interface RefreshTokenService {
    String createRefreshToken(Authentication authentication, UserEntity user);
    LoginResponse rotateRefreshToken(String refreshToken);
    void revokeAllUserTokens(UserEntity user);
}
