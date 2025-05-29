package com.agors.domain.validation;

import com.agors.infrastructure.persistence.impl.UserDaoImpl;
import com.agors.infrastructure.util.I18n;

public class SettingsValidator {

    private final UserDaoImpl userDao = new UserDaoImpl();

    public String validateUsername(String newUsername, String currentUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return I18n.get("username_empty");
        }
        if (!newUsername.matches("[a-zA-Z0-9]{5,30}")) {
            return I18n.get("username_invalid");
        }
        if (newUsername.equals(currentUsername)) {
            return I18n.get("username_same");
        }
        if (userDao.existsByUsername(newUsername)) {
            return I18n.get("username_exists");
        }
        return null;
    }

    public String validateEmail(String newEmail, String currentEmail) {
        if (newEmail == null || newEmail.trim().isEmpty()) {
            return I18n.get("email_empty");
        }
        if (!newEmail.matches("[a-zA-Z0-9.]{6,30}@[a-zA-Z0-9.]+\\.[a-zA-Z]{2,}")) {
            return I18n.get("email_invalid");
        }
        if (!newEmail.equals(currentEmail) && userDao.existsByEmail(newEmail)) {
            return I18n.get("email_exists");
        }
        return null;
    }

    public String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return I18n.get("password_empty");
        }
        if (password.length() < 6) {
            return I18n.get("password_short");
        }
        return null;
    }
}
