package com.project.service;

import com.project.dto.Mapper;
import com.project.dto.UserResponse;
import com.project.entity.User;
import com.project.exception.BadRequestException;
import com.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse uploadCv(MultipartFile file, User candidate) {
        if (file.isEmpty()) {
            throw new BadRequestException("File CV khong duoc rong");
        }
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new BadRequestException("Chi chap nhan file PDF");
        }

        try {
            Path uploadDir = Path.of("uploads", "cv");
            Files.createDirectories(uploadDir);
            String fileName = UUID.randomUUID() + ".pdf";
            Path target = uploadDir.resolve(fileName);
            file.transferTo(target);

            candidate.setCvUrl("/uploads/cv/" + fileName);
            return Mapper.toUserResponse(userRepository.save(candidate));
        } catch (IOException ex) {
            throw new BadRequestException("Upload CV that bai: " + ex.getMessage());
        }
    }
}
