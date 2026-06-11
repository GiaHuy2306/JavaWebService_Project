package com.project.service;

import com.project.dto.*;
import com.project.dto.request.*;
import com.project.dto.response.AuthResponse;
import com.project.dto.response.UserResponse;
import com.project.entity.RefreshToken;
import com.project.entity.User;
import com.project.exception.BadRequestException;
import com.project.exception.ConflictException;
import com.project.exception.ForbiddenException;
import com.project.enums.Role;
import com.project.enums.UserStatus;
import com.project.repository.RefreshTokenRepository;
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

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final int TEMP_PASSWORD_LENGTH = 8;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTokenBlacklistService redisTokenBlacklistService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;

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
                .role(Role.CANDIDATE)
                .companyName(null)
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
        redisTokenBlacklistService.addToBlacklist(accessToken, jwtProvider.getExpiryDate(accessToken));
    }

    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadRequestException("Mat khau cu khong dung");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadRequestException("Thong tin tai khoan khong dung"));
        if (!user.getEmail().equalsIgnoreCase(request.email())) {
            throw new BadRequestException("Email khong khop voi tai khoan");
        }

        String temporaryPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);
        emailService.sendTemporaryPassword(user.getEmail(), temporaryPassword);
    }

    private String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            int index = SECURE_RANDOM.nextInt(TEMP_PASSWORD_CHARS.length());
            password.append(TEMP_PASSWORD_CHARS.charAt(index));
        }
        return password.toString();
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
