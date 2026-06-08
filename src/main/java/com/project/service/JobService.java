package com.project.service;

import com.project.dto.JobRequest;
import com.project.dto.JobResponse;
import com.project.dto.Mapper;
import com.project.entity.Job;
import com.project.entity.User;
import com.project.exception.ForbiddenException;
import com.project.exception.NotFoundException;
import com.project.model.JobStatus;
import com.project.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;

    public List<JobResponse> getOpenJobs(String keyword) {
        List<Job> jobs = keyword == null || keyword.isBlank()
                ? jobRepository.findByStatus(JobStatus.OPEN)
                : jobRepository.findByStatusAndTitleContainingIgnoreCase(JobStatus.OPEN, keyword);
        return jobs.stream().map(Mapper::toJobResponse).collect(Collectors.toList());
    }

    public List<JobResponse> getAllJobsForAdmin() {
        return jobRepository.findAll()
                .stream()
                .map(Mapper::toJobResponse)
                .collect(Collectors.toList());
    }

    public List<JobResponse> getJobsByEmployer(User employer) {
        return jobRepository.findByEmployer(employer)
                .stream()
                .map(Mapper::toJobResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobResponse createJob(JobRequest request, User employer) {
        Job job = Job.builder()
                .title(request.title())
                .description(request.description())
                .location(request.location())
                .salaryMin(request.salaryMin())
                .salaryMax(request.salaryMax())
                .deadline(request.deadline())
                .status(JobStatus.PENDING)
                .employer(employer)
                .build();
        return Mapper.toJobResponse(jobRepository.save(job));
    }

    @Transactional
    public JobResponse updateJob(Long id, JobRequest request, User employer) {
        Job job = findJob(id);
        checkOwner(job, employer);
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setLocation(request.location());
        job.setSalaryMin(request.salaryMin());
        job.setSalaryMax(request.salaryMax());
        job.setDeadline(request.deadline());
        job.setStatus(JobStatus.PENDING);
        return Mapper.toJobResponse(jobRepository.save(job));
    }

    @Transactional
    public JobResponse changeJobStatus(Long id, JobStatus status) {
        Job job = findJob(id);
        job.setStatus(status);
        return Mapper.toJobResponse(jobRepository.save(job));
    }

    @Transactional
    public void deleteJob(Long id, User employer) {
        Job job = findJob(id);
        checkOwner(job, employer);
        jobRepository.delete(job);
    }

    public Job findJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Khong tim thay job id = " + id));
    }

    private void checkOwner(Job job, User employer) {
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new ForbiddenException("Chi employer tao job moi duoc sua/xoa job nay");
        }
    }
}
