package com.agors.domain.validation;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SignupValidatorTest {

    @Test
    void testEmptyFields() {
        Map<String, String> errors = SignupValidator.validate("", "", "");
        assertEquals(3, errors.size());
        assertTrue(errors.containsKey("username"));
        assertTrue(errors.containsKey("email"));
        assertTrue(errors.containsKey("password"));
    }

    @Test
    void testInvalidFormat() {
        Map<String, String> errors = SignupValidator.validate("abc", "bad@_email", "123");
        assertTrue(errors.containsKey("username"));
        assertTrue(errors.containsKey("email"));
        assertTrue(errors.containsKey("password"));
    }

    @Test
    void testAllValidOrAlreadyUsed() {
        Map<String, String> errors = SignupValidator.validate("admin", "admin@example.com", "securepass");
        assertTrue(errors.isEmpty() || errors.containsKey("username") || errors.containsKey("email"));
    }
}
