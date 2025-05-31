package com.agors.domain.validation;

import com.agors.infrastructure.persistence.impl.UserDaoImpl;
import com.agors.infrastructure.util.I18n;

/**
 * Клас {@code SettingsValidator} відповідає за валідацію налаштувань користувача
 * при оновленні імені користувача, email або пароля.
 * <p>
 * Перевірка включає:
 * <ul>
 *   <li>Формат і довжину введених значень</li>
 *   <li>Збіг із поточними значеннями</li>
 *   <li>Унікальність у базі даних</li>
 * </ul>
 * Всі повідомлення локалізуються через {@link I18n}.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class SettingsValidator {

    /** DAO для перевірки унікальності логіна та email. */
    private final UserDaoImpl userDao = new UserDaoImpl();

    /**
     * Перевіряє нове ім’я користувача на валідність.
     *
     * @param newUsername новий логін
     * @param currentUsername поточний логін користувача
     * @return локалізоване повідомлення про помилку або {@code null}, якщо все коректно
     */
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

    /**
     * Перевіряє новий email на валідність.
     *
     * @param newEmail новий email
     * @param currentEmail поточний email користувача
     * @return локалізоване повідомлення про помилку або {@code null}, якщо все коректно
     */
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

    /**
     * Перевіряє новий пароль на мінімальні вимоги.
     *
     * @param password новий пароль
     * @return локалізоване повідомлення про помилку або {@code null}, якщо пароль валідний
     */
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
