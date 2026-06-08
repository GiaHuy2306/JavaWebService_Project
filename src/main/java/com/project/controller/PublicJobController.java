package com.project.controller;

import com.project.dto.ApiResponse;
import com.project.dto.JobResponse;
import com.project.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class PublicJobController {
    private final JobService jobService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> getOpenJobs(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(ApiResponse.ok("Lay danh sach job dang mo thanh cong", jobService.getOpenJobs(keyword)));
    }
}
