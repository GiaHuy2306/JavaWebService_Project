package com.project.dto.request;

import com.project.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
        @NotBlank @Email String email,
        @NotBlank String fullName,
        @NotNull UserStatus status,
        String companyName
) {
}
