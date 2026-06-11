package com.project.dto.response;

import com.project.enums.Role;
import com.project.enums.UserStatus;

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
