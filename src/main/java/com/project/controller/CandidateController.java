package com.project.controller;

import com.project.dto.request.ApplicationRequest;
import com.project.dto.response.ApiResponse;
import com.project.dto.response.ApplicationResponse;
import com.project.dto.response.JobResponse;
import com.project.dto.response.UserResponse;
import com.project.entity.User;
import com.project.service.ApplicationService;
import com.project.service.CurrentUserService;
import com.project.service.FileStorageService;
import com.project.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/candidate")
@RequiredArgsConstructor
public class CandidateController {
    private final CurrentUserService currentUserService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final FileStorageService fileStorageService;

    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<JobResponse>>> searchJobs(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(ApiResponse.ok("Tim kiem job thanh cong", jobService.getOpenJobs(keyword)));
    }

    @PostMapping("/applications")
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(@Valid @RequestBody ApplicationRequest request) {
        User candidate = currentUserService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Nop ho so thanh cong", applicationService.apply(request, candidate)));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getMyApplications() {
        User candidate = currentUserService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok("Lay danh sach ho so cua candidate thanh cong", applicationService.getCandidateApplications(candidate)));
    }

    @PostMapping("/cv/upload")
    public ResponseEntity<ApiResponse<UserResponse>> uploadCv(@RequestParam("file") MultipartFile file) {
        User candidate = currentUserService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok("Upload CV thanh cong", fileStorageService.uploadCv(file, candidate)));
    }
}
