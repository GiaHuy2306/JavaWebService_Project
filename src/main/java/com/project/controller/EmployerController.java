package com.project.controller;

import com.project.dto.*;
import com.project.entity.User;
import com.project.service.ApplicationService;
import com.project.service.CurrentUserService;
import com.project.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employer")
@RequiredArgsConstructor
public class EmployerController {
    private final CurrentUserService currentUserService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getMyJobs() {
        User employer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok("Lay danh sach job cua employer thanh cong", jobService.getJobsByEmployer(employer)));
    }

    @PostMapping("/jobs")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(@Valid @RequestBody JobRequest request) {
        User employer = currentUserService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Tao job thanh cong, cho admin duyet", jobService.createJob(request, employer)));
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequest request) {
        User employer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok("Cap nhat job thanh cong, cho admin duyet lai", jobService.updateJob(id, request, employer)));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        User employer = currentUserService.getCurrentUser();
        jobService.deleteJob(id, employer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplications() {
        User employer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok("Lay danh sach ho so ung tuyen thanh cong", applicationService.getEmployerApplications(employer)));
    }

    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusRequest request) {
        User employer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok("Cap nhat trang thai ho so thanh cong", applicationService.updateStatus(id, request, employer)));
    }
}
