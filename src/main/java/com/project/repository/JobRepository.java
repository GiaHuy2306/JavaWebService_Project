package com.project.repository;

import com.project.entity.Job;
import com.project.entity.User;
import com.project.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByEmployer(User employer);

    List<Job> findByStatus(JobStatus status);

    List<Job> findByStatusAndTitleContainingIgnoreCase(JobStatus status, String title);
}
