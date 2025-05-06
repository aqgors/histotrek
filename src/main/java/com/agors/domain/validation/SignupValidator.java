package com.agors.domain.validation;

import com.agors.infrastructure.persistence.dao.UserDao;
import com.agors.domain.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupValidator {

    public static Map<String, String> validate(String username, String email, String password) {
        Map<String, String> errors = new HashMap<>();

        if (username == null || username.isEmpty()) {
            errors.put("username", "Username is required");
        } else if (!username.matches("^(?![._])(?!.*[_.]{2})[a-zA-Z0-9._]{5,30}(?<![_.])$")) {
            errors.put("username", "Only letters, digits, . or _, 5–30 chars, no __ or ..");
        }

        if (email == null || email.isEmpty()) {
            errors.put("email", "Email is required");
        } else if (!email.matches("^[a-zA-Z0-9.]{6,30}@[a-zA-Z0-9.]+\\.[a-zA-Z]{2,}$") || email.contains("_")) {
            errors.put("email", "Invalid email format (no _ allowed)");
        }

        if (password == null || password.isEmpty()) {
            errors.put("password", "Password is required");
        } else if (password.length() < 6) {
            errors.put("password", "Password must be at least 6 characters");
        }

        // Перевірка на унікальність
        UserDao dao = new UserDao();
        List<User> users = dao.getAllUsers();
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                errors.put("username", "Username already taken");
            }
            if (u.getEmail().equals(email)) {
                errors.put("email", "Email already registered");
            }
        }

        return errors;
    }

}
