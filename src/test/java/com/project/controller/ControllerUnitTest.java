package com.project.controller;

import com.project.dto.request.ApplicationRequest;
import com.project.dto.request.JobRequest;
import com.project.dto.response.ApplicationResponse;
import com.project.dto.response.JobResponse;
import com.project.dto.response.UserResponse;
import com.project.entity.User;
import com.project.enums.ApplicationStatus;
import com.project.enums.JobStatus;
import com.project.enums.Role;
import com.project.enums.UserStatus;
import com.project.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ControllerUnitTest {
    @Test
    void adminGetUsersReturnsPage() {
        UserService userService = mock(UserService.class);
        JobService jobService = mock(JobService.class);
        AdminController controller = new AdminController(userService, jobService);
        when(userService.searchUsers(anyString(), any())).thenReturn(new PageImpl<>(List.of(userResponse())));

        var response = controller.getUsers("admin", 0, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().data().getTotalElements());
    }

    @Test
    void adminChangeJobStatusReturnsJob() {
        UserService userService = mock(UserService.class);
        JobService jobService = mock(JobService.class);
        AdminController controller = new AdminController(userService, jobService);
        when(jobService.changeJobStatus(1L, JobStatus.OPEN)).thenReturn(jobResponse(JobStatus.OPEN));

        var response = controller.changeJobStatus(1L, JobStatus.OPEN);

        assertEquals(JobStatus.OPEN, response.getBody().data().status());
    }

    @Test
    void employerCreateJobReturnsCreated() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        JobService jobService = mock(JobService.class);
        ApplicationService applicationService = mock(ApplicationService.class);
        EmployerController controller = new EmployerController(currentUserService, jobService, applicationService);
        User employer = sampleUser(Role.EMPLOYER);
        when(currentUserService.getCurrentUser()).thenReturn(employer);
        when(jobService.createJob(any(JobRequest.class), eq(employer))).thenReturn(jobResponse(JobStatus.PENDING));

        var response = controller.createJob(jobRequest());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(JobStatus.PENDING, response.getBody().data().status());
    }

    @Test
    void candidateApplyReturnsCreated() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        JobService jobService = mock(JobService.class);
        ApplicationService applicationService = mock(ApplicationService.class);
        FileStorageService fileStorageService = mock(FileStorageService.class);
        CandidateController controller = new CandidateController(currentUserService, jobService, applicationService, fileStorageService);
        User candidate = sampleUser(Role.CANDIDATE);
        when(currentUserService.getCurrentUser()).thenReturn(candidate);
        when(applicationService.apply(any(ApplicationRequest.class), eq(candidate))).thenReturn(applicationResponse());

        var response = controller.apply(new ApplicationRequest(1L, "Cover letter", "/cv.pdf"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(ApplicationStatus.PENDING, response.getBody().data().status());
    }

    @Test
    void candidateUploadCvReturnsUser() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        JobService jobService = mock(JobService.class);
        ApplicationService applicationService = mock(ApplicationService.class);
        FileStorageService fileStorageService = mock(FileStorageService.class);
        CandidateController controller = new CandidateController(currentUserService, jobService, applicationService, fileStorageService);
        User candidate = sampleUser(Role.CANDIDATE);
        MockMultipartFile file = new MockMultipartFile("file", "cv.pdf", "application/pdf", "demo".getBytes());
        when(currentUserService.getCurrentUser()).thenReturn(candidate);
        when(fileStorageService.uploadCv(file, candidate)).thenReturn(userResponse());

        var response = controller.uploadCv(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("candidate", response.getBody().data().username());
    }

    private UserResponse userResponse() {
        return new UserResponse(1L, "candidate", "candidate@gmail.com", "Candidate", Role.CANDIDATE, UserStatus.ACTIVE, null, null);
    }

    private JobRequest jobRequest() {
        return new JobRequest("Java Intern", "Learn Spring", "Ha Noi", 300.0, 500.0, LocalDate.now().plusDays(30));
    }

    private JobResponse jobResponse(JobStatus status) {
        return new JobResponse(1L, "Java Intern", "Learn Spring", "Ha Noi", 300.0, 500.0,
                LocalDate.now().plusDays(30), status, 2L, "Employer", null);
    }

    private ApplicationResponse applicationResponse() {
        return new ApplicationResponse(1L, 1L, "Java Intern", 3L, "Candidate", "Cover letter",
                "/cv.pdf", ApplicationStatus.PENDING, null, null);
    }

    private User sampleUser(Role role) {
        return User.builder()
                .id(1L)
                .username(role.name().toLowerCase())
                .email(role.name().toLowerCase() + "@gmail.com")
                .fullName(role.name())
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
    }
}
