package com.project.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;

class JwtProviderTest {
    @Test
    void validateTokenReturnsFalseForInvalidToken() {
        JwtProvider jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "jwtSecret", "01234567890123456789012345678901");

        assertFalse(jwtProvider.validateToken("invalid-token"));
    }
}
