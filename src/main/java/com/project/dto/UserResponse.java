package com.project.dto;

import com.project.model.Role;
import com.project.model.UserStatus;

public record UserResponse(
        Long id,
        String username,
        String email,
        String fullName,
        Role role,
        UserStatus status,
        String companyName,
        String cvUrl
) {
}
