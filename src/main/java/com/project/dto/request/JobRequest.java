package com.project.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record JobRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String location,
        Double salaryMin,
        Double salaryMax,
        @NotNull @FutureOrPresent LocalDate deadline
) {
}
