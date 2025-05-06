package com.agors.domain.validation;

import com.agors.infrastructure.persistence.dao.UserDao;
import com.agors.domain.entity.User;
import com.agors.infrastructure.util.PasswordUtil;

import java.util.HashMap;
import java.util.Map;

public class LoginValidator {

    /**
     * Повертає Map помилок: ключі "login" (username/email) і "password"
     * Якщо все добре — повертає пустий Map
     */
    public static Map<String, String> validate(String loginOrEmail, String rawPassword) {
        Map<String, String> errors = new HashMap<>();

        if (loginOrEmail == null || loginOrEmail.isBlank()) {
            errors.put("login", "Enter username or email");
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            errors.put("password", "Enter your password");
        }

        if (errors.isEmpty()) {
            UserDao dao = new UserDao();
            User user = dao.getByUsernameOrEmail(loginOrEmail);

            if (user == null) {
                errors.put("login", "No user found");
            } else {
                String hashed = PasswordUtil.hashPassword(rawPassword);
                if (!hashed.equals(user.getPasswordHash())) {
                    errors.put("password", "Incorrect password");
                }
            }
        }

        return errors;
    }
}
