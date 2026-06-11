package com.project.dto.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Object errors,
        int status,
        LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, null, HttpStatus.OK.value(), LocalDateTime.now());
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(true, message, data, null, HttpStatus.CREATED.value(), LocalDateTime.now());
    }

    public static ApiResponse<Void> error(String message, Object errors, HttpStatus status) {
        return new ApiResponse<>(false, message, null, errors, status.value(), LocalDateTime.now());
    }
}
