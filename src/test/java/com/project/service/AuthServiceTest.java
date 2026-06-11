package com.project.service;

import com.project.dto.request.ChangePasswordRequest;
import com.project.dto.request.ForgotPasswordRequest;
import com.project.dto.request.RegisterRequest;
import com.project.entity.User;
import com.project.exception.BadRequestException;
import com.project.exception.ConflictException;
import com.project.enums.Role;
import com.project.enums.UserStatus;
import com.project.repository.RefreshTokenRepository;
import com.project.repository.UserRepository;
import com.project.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private RedisTokenBlacklistService redisTokenBlacklistService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerSuccess() {
        RegisterRequest request = new RegisterRequest(
                "candidate2",
                "candidate2@gmail.com",
                "123456",
                "Candidate Two"
        );
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = authService.register(request);

        assertEquals("candidate2", response.username());
        assertEquals(Role.CANDIDATE, response.role());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerDuplicateUsernameThrowsConflict() {
        RegisterRequest request = new RegisterRequest(
                "candidate",
                "candidate@gmail.com",
                "123456",
                "Candidate"
        );
        when(userRepository.existsByUsername("candidate")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(request));
    }

    @Test
    void changePasswordSuccess() {
        User user = sampleUser();
        when(passwordEncoder.matches("123456", "encoded-old")).thenReturn(true);
        when(passwordEncoder.encode("654321")).thenReturn("encoded-new");

        authService.changePassword(user, new ChangePasswordRequest("123456", "654321"));

        assertEquals("encoded-new", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordWrongOldPasswordThrowsBadRequest() {
        User user = sampleUser();
        when(passwordEncoder.matches("wrong", "encoded-old")).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> authService.changePassword(user, new ChangePasswordRequest("wrong", "654321")));
    }

    @Test
    void forgotPasswordSuccess() {
        User user = sampleUser();
        when(userRepository.findByUsername("candidate")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-new");

        authService.forgotPassword(new ForgotPasswordRequest("candidate", "candidate@gmail.com"));

        assertEquals("encoded-new", user.getPassword());
        verify(userRepository).save(user);
        verify(emailService).sendTemporaryPassword(eq("candidate@gmail.com"), anyString());
    }

    private User sampleUser() {
        return User.builder()
                .id(1L)
                .username("candidate")
                .email("candidate@gmail.com")
                .password("encoded-old")
                .fullName("Candidate")
                .role(Role.CANDIDATE)
                .status(UserStatus.ACTIVE)
                .build();
    }
}
