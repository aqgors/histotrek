package com.agors.domain.validation;

import com.agors.infrastructure.persistence.impl.UserDaoImpl;
import com.agors.domain.entity.User;
import com.agors.infrastructure.util.PasswordUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * Валідатор даних для входу користувача.
 * <p>
 * Перевіряє логін (або email) та пароль, повертає карту помилок,
 * де ключі — це поля вводу, а значення — ключі повідомлень для I18n.
 * Якщо дані валідні, повертається порожня карта.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class LoginValidator {

    /**
     * Виконує валідацію введеного логіну (або email) та пароля.
     *
     * @param loginOrEmail логін або email користувача
     * @param rawPassword  пароль у відкритому вигляді
     * @return Map помилок: ключі "login" та "password" з ключами повідомлень; пустий, якщо без помилок
     */
    public static Map<String, String> validate(String loginOrEmail, String rawPassword) {
        Map<String, String> errors = new HashMap<>();

        if (loginOrEmail == null || loginOrEmail.isBlank()) {
            errors.put("login", "error_login_required");
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            errors.put("password", "error_password_required");
        }

        if (errors.isEmpty()) {
            UserDaoImpl dao = new UserDaoImpl();
            User user = dao.getByUsernameOrEmail(loginOrEmail);

            if (user == null) {
                errors.put("login", "error_user_not_found");
            } else {
                String hashed = PasswordUtil.hashPassword(rawPassword);
                if (!hashed.equals(user.getPasswordHash())) {
                    errors.put("password", "error_wrong_password");
                }
            }
        }

        return errors;
    }
}