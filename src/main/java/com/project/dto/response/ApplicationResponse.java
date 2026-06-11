package com.project.dto.response;

import com.project.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponse(
        Long id,
        Long jobId,
        String jobTitle,
        Long candidateId,
        String candidateName,
        String coverLetter,
        String cvUrl,
        ApplicationStatus status,
        String employerNote,
        LocalDateTime createdAt
) {
}
