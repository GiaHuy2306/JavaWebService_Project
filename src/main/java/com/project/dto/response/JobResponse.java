package com.project.dto.response;

import com.project.enums.JobStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record JobResponse(
        Long id,
        String title,
        String description,
        String location,
        Double salaryMin,
        Double salaryMax,
        LocalDate deadline,
        JobStatus status,
        Long employerId,
        String employerName,
        LocalDateTime createdAt
) {
}
