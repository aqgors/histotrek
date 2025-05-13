package com.agors.domain.validation;

import com.agors.infrastructure.persistence.impl.UserDaoImpl;
import com.agors.domain.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Валідатор даних для реєстрації користувача.
 * <p>
 * Перевіряє валідність логіну, email та пароля,
 * а також контролює унікальність імені користувача та адреси електронної пошти.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class SignupValidator {

    /**
     * Виконує валідацію даних реєстрації.
     *
     * @param username ім'я користувача
     * @param email    електронна пошта
     * @param password пароль у відкритому вигляді
     * @return Map помилок: ключі "username", "email", "password" з повідомленнями; пустий, якщо без помилок
     */
    public static Map<String, String> validate(String username, String email, String password) {
        Map<String, String> errors = new HashMap<>();

        if (username == null || username.isEmpty()) {
            errors.put("username", "Ім'я користувача обов'язкове");
        } else if (!username.matches("^(?![._])(?!.*[_.]{2})[a-zA-Z0-9._]{5,30}(?<![_.])$")) {
            errors.put("username", "Лише літери, цифри, . або _, 5–30 символів, без __ чи ..");
        }

        if (email == null || email.isEmpty()) {
            errors.put("email", "Email обов'язковий");
        } else if (!email.matches("^[a-zA-Z0-9.]{6,30}@[a-zA-Z0-9.]+\\.[a-zA-Z]{2,}$") || email.contains("_")) {
            errors.put("email", "Неправильний формат email ( _ заборонено)");
        }

        if (password == null || password.isEmpty()) {
            errors.put("password", "Пароль обов'язковий");
        } else if (password.length() < 6) {
            errors.put("password", "Пароль має містити щонайменше 6 символів");
        }

        UserDaoImpl dao = new UserDaoImpl();
        List<User> users = dao.getAllUsers();
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                errors.put("username", "Ім'я вже використовується");
            }
            if (u.getEmail().equals(email)) {
                errors.put("email", "Email вже зареєстровано");
            }
        }

        return errors;
    }
}
