package com.project.config;

import com.project.entity.User;
import com.project.enums.Role;
import com.project.enums.UserStatus;
import com.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createUser("admin", "admin@gmail.com", "Admin", Role.ADMIN, null);
        createUser("employer", "employer@gmail.com", "Employer Demo", Role.EMPLOYER, "Demo Company");
        createUser("candidate", "candidate@gmail.com", "Candidate Demo", Role.CANDIDATE, null);
    }

    private void createUser(String username, String email, String fullName, Role role, String companyName) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        userRepository.save(User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode("123456"))
                .fullName(fullName)
                .role(role)
                .companyName(companyName)
                .status(UserStatus.ACTIVE)
                .build());
    }
}
