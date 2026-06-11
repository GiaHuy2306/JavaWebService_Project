package com.project.dto;

import com.project.dto.response.ApplicationResponse;
import com.project.dto.response.JobResponse;
import com.project.dto.response.UserResponse;
import com.project.entity.Job;
import com.project.entity.JobApplication;
import com.project.entity.User;

public class Mapper {
    private Mapper() {
    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getStatus(),
                user.getCompanyName(),
                user.getCvUrl()
        );
    }

    public static JobResponse toJobResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getLocation(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getDeadline(),
                job.getStatus(),
                job.getEmployer().getId(),
                job.getEmployer().getFullName(),
                job.getCreatedAt()
        );
    }

    public static ApplicationResponse toApplicationResponse(JobApplication application) {
        return new ApplicationResponse(
                application.getId(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                application.getCandidate().getId(),
                application.getCandidate().getFullName(),
                application.getCoverLetter(),
                application.getCvUrl(),
                application.getStatus(),
                application.getEmployerNote(),
                application.getCreatedAt()
        );
    }
}
