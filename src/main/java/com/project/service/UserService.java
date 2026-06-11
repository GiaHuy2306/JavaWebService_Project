package com.project.service;

import com.project.dto.Mapper;
import com.project.dto.request.ChangeUserRoleRequest;
import com.project.dto.response.UserResponse;
import com.project.dto.request.UserUpdateRequest;
import com.project.entity.User;
import com.project.enums.Role;
import com.project.exception.NotFoundException;
import com.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(Mapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public Page<UserResponse> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return userRepository.findAll(pageable).map(Mapper::toUserResponse);
        }
        return userRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                        keyword,
                        keyword,
                        keyword,
                        pageable
                )
                .map(Mapper::toUserResponse);
    }

    public UserResponse getUserById(Long id) {
        return Mapper.toUserResponse(findUser(id));
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = findUser(id);
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setStatus(request.status());
        user.setCompanyName(request.companyName());
        return Mapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse changeRole(Long id, ChangeUserRoleRequest request) {
        User user = findUser(id);
        user.setRole(request.role());
        if (request.role() == Role.EMPLOYER) {
            user.setCompanyName(request.companyName());
        } else {
            user.setCompanyName(null);
        }
        return Mapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUser(id);
        userRepository.delete(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Khong tim thay user id = " + id));
    }
}
