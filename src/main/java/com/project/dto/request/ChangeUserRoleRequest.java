package com.project.dto.request;

import com.project.enums.Role;
import jakarta.validation.constraints.NotNull;

public record ChangeUserRoleRequest(
        @NotNull Role role,
        String companyName
) {
}
