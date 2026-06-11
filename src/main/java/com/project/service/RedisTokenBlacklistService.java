package com.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisTokenBlacklistService {
    private static final String PREFIX = "blacklist:";

    private final StringRedisTemplate redisTemplate;

    public void addToBlacklist(String token, LocalDateTime expiryDate) {
        long seconds = Duration.between(LocalDateTime.now(), expiryDate).getSeconds();
        if (seconds <= 0) {
            seconds = 1;
        }
        try {
            redisTemplate.opsForValue().set(PREFIX + token, "revoked", Duration.ofSeconds(seconds));
        } catch (RuntimeException ex) {
            log.warn("Redis unavailable, cannot blacklist token: {}", ex.getMessage());
        }
    }

    public boolean isBlacklisted(String token) {
        try {
            Boolean exists = redisTemplate.hasKey(PREFIX + token);
            return Boolean.TRUE.equals(exists);
        } catch (RuntimeException ex) {
            log.warn("Redis unavailable, skip blacklist check: {}", ex.getMessage());
            return false;
        }
    }
}
