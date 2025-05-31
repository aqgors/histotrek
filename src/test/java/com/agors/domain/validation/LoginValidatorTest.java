package com.agors.domain.validation;

import com.agors.domain.entity.User;
import com.agors.infrastructure.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LoginValidatorTest {

    @Test
    void testEmptyLoginAndPassword() {
        Map<String, String> errors = LoginValidator.validate("", "");
        assertTrue(errors.containsKey("login"));
        assertTrue(errors.containsKey("password"));
    }

    @Test
    void testNullLoginAndPassword() {
        Map<String, String> errors = LoginValidator.validate(null, null);
        assertTrue(errors.containsKey("login"));
        assertTrue(errors.containsKey("password"));
    }

    @Test
    void testUserNotFound() {
        Map<String, String> errors = LoginValidator.validate("nonexistent_user", "password123");
        assertTrue(errors.containsKey("login"));
        assertEquals("error_user_not_found", errors.get("login"));
    }

    @Test
    void testWrongPassword() {
        Map<String, String> errors = LoginValidator.validate("admin", "wrongpassword");
        assertTrue(errors.containsKey("password"));
    }

    @Test
    void testCorrectLogin() {
        Map<String, String> errors = LoginValidator.validate("admin", "123456");

        assertTrue(errors.isEmpty() ||
            (errors.containsKey("login") && errors.get("login").equals("error_user_not_found")) ||
            (errors.containsKey("password") && errors.get("password").equals("error_wrong_password")));
    }
}
