package com.project.aop;

import com.project.dto.response.ApplicationResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ApplicationLogAspect {
    @AfterReturning(
            pointcut = "execution(* com.project.service.ApplicationService.apply(..))",
            returning = "result"
    )
    public void logApplySuccess(Object result) {
        ApplicationResponse response = (ApplicationResponse) result;
        log.info("Candidate ID: {} applied for Job ID: {}", response.candidateId(), response.jobId());
    }

    @AfterThrowing(
            pointcut = "execution(* com.project.service.ApplicationService.apply(..))",
            throwing = "ex"
    )
    public void logApplyError(Throwable ex) {
        log.warn("Apply job failed: {}", ex.getMessage());
    }
}
