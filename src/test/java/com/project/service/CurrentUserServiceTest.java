package com.project.service;

import com.project.entity.User;
import com.project.enums.Role;
import com.project.enums.UserStatus;
import com.project.exception.ForbiddenException;
import com.project.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrentUserServiceTest {
    private final CurrentUserService currentUserService = new CurrentUserService();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserReturnsUserFromPrincipal() {
        User user = User.builder()
                .id(1L)
                .username("candidate")
                .password("encoded")
                .role(Role.CANDIDATE)
                .status(UserStatus.ACTIVE)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

        assertEquals(user, currentUserService.getCurrentUser());
    }

    @Test
    void getCurrentUserWithoutCustomPrincipalThrowsForbidden() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", null));

        assertThrows(ForbiddenException.class, currentUserService::getCurrentUser);
    }
}
