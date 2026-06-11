package com.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplicationRequest(
        @NotNull Long jobId,
        @NotBlank String coverLetter,
        @NotBlank String cvUrl
) {
}
