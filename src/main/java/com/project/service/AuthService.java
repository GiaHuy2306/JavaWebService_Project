package com.project.service;

import com.project.dto.*;
import com.project.entity.RefreshToken;
import com.project.entity.TokenBlacklist;
import com.project.entity.User;
import com.project.exception.BadRequestException;
import com.project.exception.ConflictException;
import com.project.exception.ForbiddenException;
import com.project.model.UserStatus;
import com.project.repository.RefreshTokenRepository;
import com.project.repository.TokenBlacklistRepository;
import com.project.repository.UserRepository;
import com.project.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Value("${jwt-refresh-expired}")
    private Long refreshExpired;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username da ton tai");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email da ton tai");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(request.role())
                .companyName(request.companyName())
                .status(UserStatus.ACTIVE)
                .build();
        return Mapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BadRequestException("Khong tim thay tai khoan"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ForbiddenException("Tai khoan da bi khoa");
        }

        String accessToken = jwtProvider.generateToken(user);
        String refreshToken = createRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken, "Bearer", Mapper.toUserResponse(user));
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BadRequestException("Refresh token khong hop le"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new BadRequestException("Refresh token da het han");
        }

        User user = refreshToken.getUser();
        String accessToken = jwtProvider.generateToken(user);
        return new AuthResponse(accessToken, refreshToken.getToken(), "Bearer", Mapper.toUserResponse(user));
    }

    @Transactional
    public void logout(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new BadRequestException("Thieu access token");
        }
        tokenBlacklistRepository.save(TokenBlacklist.builder()
                .token(accessToken)
                .expiryDate(jwtProvider.getExpiryDate(accessToken))
                .build());
    }

    private String createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusNanos(refreshExpired * 1_000_000))
                .build();
        return refreshTokenRepository.save(refreshToken).getToken();
    }
}
