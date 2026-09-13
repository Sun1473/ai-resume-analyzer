package com.ara.resumeanalyzer;

import com.ara.resumeanalyzer.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that JWT generation and validation work correctly —
 * an important thing to prove works in an auth-based project.
 */
@SpringBootTest
@ActiveProfiles("h2")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void generatedToken_shouldContainCorrectUsername() {
        String token = jwtUtil.generateToken("testuser");
        String extracted = jwtUtil.extractUsername(token);
        assertEquals("testuser", extracted);
    }

    @Test
    void token_shouldBeValid_forMatchingUsername() {
        String token = jwtUtil.generateToken("testuser");
        assertTrue(jwtUtil.isTokenValid(token, "testuser"));
    }

    @Test
    void token_shouldBeInvalid_forDifferentUsername() {
        String token = jwtUtil.generateToken("testuser");
        assertFalse(jwtUtil.isTokenValid(token, "someoneelse"));
    }
}
