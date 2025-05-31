package com.agors.infrastructure.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void hashPassword_ShouldReturnConsistentHash() {
        String password = "secure123";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);

        assertEquals(hash1, hash2, "Хеш повинен бути стабільним для одного й того самого пароля");
    }

    @Test
    void hashPassword_ShouldNotReturnPlaintext() {
        String password = "admin";
        String hashed = PasswordUtil.hashPassword(password);

        assertNotEquals(password, hashed, "Хеш не повинен бути рівний самому паролю");
        assertTrue(hashed.length() > 10, "Хеш повинен бути не короткий");
    }
}
