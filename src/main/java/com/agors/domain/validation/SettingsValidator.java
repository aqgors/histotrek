package com.agors.domain.validation;

import com.agors.infrastructure.persistence.impl.UserDaoImpl;

public class SettingsValidator {

    private final UserDaoImpl userDao = new UserDaoImpl();

    public String validateUsername(String newUsername, String currentUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return "Ім'я користувача не може бути порожнім";
        }
        if (!newUsername.matches("[a-zA-Z0-9]{5,30}")) {
            return "Ім'я має містити лише латинські букви та цифри (5-30 символів)";
        }
        if (newUsername.equals(currentUsername)) {
            return "Це вже ваше поточне ім'я користувача";
        }
        if (userDao.existsByUsername(newUsername)) {
            return "Таке ім'я вже використовується";
        }
        return null;
    }

    public String validateEmail(String newEmail, String currentEmail) {
        if (newEmail == null || newEmail.trim().isEmpty()) {
            return "Email не може бути порожнім";
        }
        if (!newEmail.matches("[a-zA-Z0-9.]{6,30}@[a-zA-Z0-9.]+\\.[a-zA-Z]{2,}")) {
            return "Некоректний формат email";
        }
        if (!newEmail.equals(currentEmail) && userDao.existsByEmail(newEmail)) {
            return "Такий email вже зареєстрований";
        }
        return null;
    }

    public String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Пароль не може бути порожнім";
        }
        if (password.length() < 6) {
            return "Пароль має бути не менше 6 символів";
        }
        return null;
    }
}
