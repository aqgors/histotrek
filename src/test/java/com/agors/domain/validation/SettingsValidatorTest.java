package com.agors.domain.validation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SettingsValidatorTest {

    private final SettingsValidator validator = new SettingsValidator();

    @Test
    void testValidateUsernameEmpty() {
        assertNotNull(validator.validateUsername("", "current"));
    }

    @Test
    void testValidateEmailFormat() {
        assertNotNull(validator.validateEmail("bademail@", "current@example.com"));
    }

    @Test
    void testValidatePasswordTooShort() {
        assertNotNull(validator.validatePassword("123"));
    }
}
