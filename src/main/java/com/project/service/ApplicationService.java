package com.project.service;

import com.project.dto.request.ApplicationRequest;
import com.project.dto.response.ApplicationResponse;
import com.project.dto.request.ApplicationStatusRequest;
import com.project.dto.Mapper;
import com.project.entity.Job;
import com.project.entity.JobApplication;
import com.project.entity.User;
import com.project.exception.ConflictException;
import com.project.exception.ForbiddenException;
import com.project.exception.NotFoundException;
import com.project.enums.ApplicationStatus;
import com.project.enums.JobStatus;
import com.project.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final JobService jobService;
    private final JobApplicationRepository applicationRepository;

    @Transactional
    public ApplicationResponse apply(ApplicationRequest request, User candidate) {
        Job job = jobService.findJob(request.jobId());
        if (job.getStatus() != JobStatus.OPEN || job.getDeadline().isBefore(LocalDate.now())) {
            throw new ConflictException("Tin tuyen dung da dong hoac het han");
        }
        if (applicationRepository.existsByJobAndCandidate(job, candidate)) {
            throw new ConflictException("Ban da nop ho so vao job nay roi");
        }

        JobApplication application = JobApplication.builder()
                .job(job)
                .candidate(candidate)
                .coverLetter(request.coverLetter())
                .cvUrl(request.cvUrl())
                .status(ApplicationStatus.PENDING)
                .build();
        return Mapper.toApplicationResponse(applicationRepository.save(application));
    }

    public List<ApplicationResponse> getCandidateApplications(User candidate) {
        return applicationRepository.findByCandidate(candidate)
                .stream()
                .map(Mapper::toApplicationResponse)
                .collect(Collectors.toList());
    }

    public List<ApplicationResponse> getEmployerApplications(User employer) {
        return applicationRepository.findByJobEmployer(employer)
                .stream()
                .map(Mapper::toApplicationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponse updateStatus(Long id, ApplicationStatusRequest request, User employer) {
        JobApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Khong tim thay application id = " + id));
        if (!application.getJob().getEmployer().getId().equals(employer.getId())) {
            throw new ForbiddenException("Chi employer cua job moi duoc cap nhat ho so");
        }
        application.setStatus(request.status());
        application.setEmployerNote(request.employerNote());
        return Mapper.toApplicationResponse(applicationRepository.save(application));
    }
}
