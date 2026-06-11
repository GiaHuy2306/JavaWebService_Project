package com.project.service;

import com.project.entity.User;
import com.project.exception.ForbiddenException;
import com.project.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null
                || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new ForbiddenException("Chua dang nhap");
        }
        return userDetails.getUser();
    }
}
