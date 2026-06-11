package com.project.dto.request;

import com.project.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record ApplicationStatusRequest(
        @NotNull ApplicationStatus status,
        String employerNote
) {
}
