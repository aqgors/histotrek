package com.agors.infrastructure.util;

import java.security.MessageDigest;

/**
 * Утилітний клас для хешування паролів.
 * <p>
 * Забезпечує генерацію SHA-256 хешу для вхідного пароля.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class PasswordUtil {

    /**
     * Генерує SHA-256 хеш для заданого пароля.
     *
     * @param password пароль у відкритому вигляді
     * @return хеш-представлення пароля у шістнадцятковому форматі
     * @throws RuntimeException у разі помилки алгоритму хешування
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash)
                hexString.append(String.format("%02x", b));
            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException("Помилка хешування пароля", e);
        }
    }
}
