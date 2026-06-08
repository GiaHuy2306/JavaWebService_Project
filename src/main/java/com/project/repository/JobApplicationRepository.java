package com.project.repository;

import com.project.entity.Job;
import com.project.entity.JobApplication;
import com.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByJobAndCandidate(Job job, User candidate);

    List<JobApplication> findByCandidate(User candidate);

    List<JobApplication> findByJobEmployer(User employer);
}
