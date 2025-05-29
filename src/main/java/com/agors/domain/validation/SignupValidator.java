package com.agors.domain.validation;

import com.agors.domain.entity.User;
import com.agors.infrastructure.persistence.impl.UserDaoImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Валідатор даних для реєстрації користувача.
 * Перевіряє логін, email, пароль, а також унікальність.
 */
public class SignupValidator {

    /**
     * Валідує дані для реєстрації.
     *
     * @param username ім'я користувача
     * @param email    email
     * @param password пароль
     * @return map з ключами "username", "email", "password" — значення це ключі для перекладу
     */
    public static Map<String, String> validate(String username, String email, String password) {
        Map<String, String> errors = new HashMap<>();

        if (username == null || username.isEmpty()) {
            errors.put("username", "error_username_required");
        } else if (!username.matches("^(?![._])(?!.*[_.]{2})[a-zA-Z0-9._]{5,30}(?<![_.])$")) {
            errors.put("username", "error_username_invalid");
        }

        if (email == null || email.isEmpty()) {
            errors.put("email", "error_email_required");
        } else if (!email.matches("^[a-zA-Z0-9.]{6,30}@[a-zA-Z0-9.]+\\.[a-zA-Z]{2,}$") || email.contains("_")) {
            errors.put("email", "error_email_invalid");
        }

        if (password == null || password.isEmpty()) {
            errors.put("password", "error_password_sign_required");
        } else if (password.length() < 6) {
            errors.put("password", "error_password_short");
        }

        UserDaoImpl dao = new UserDaoImpl();
        List<User> users = dao.getAllUsers();
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                errors.put("username", "error_username_taken");
            }
            if (u.getEmail().equals(email)) {
                errors.put("email", "error_email_taken");
            }
        }

        return errors;
    }
}
