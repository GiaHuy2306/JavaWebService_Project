package com.project.dto;

import com.project.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record ApplicationStatusRequest(
        @NotNull ApplicationStatus status,
        String employerNote
) {
}
