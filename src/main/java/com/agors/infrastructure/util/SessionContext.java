package com.agors.infrastructure.util;

import com.agors.domain.entity.User;

/**
 * Контекст поточного користувача.
 * <p>
 * Дозволяє зберігати авторизованого користувача під час сесії програми.
 * </p>
 */
public class SessionContext {

    private static User currentUser;

    /**
     * Повертає поточного користувача.
     *
     * @return користувач або null, якщо не авторизовано
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Встановлює поточного користувача.
     *
     * @param user авторизований користувач
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Оновлює роль поточного користувача в сесії.
     *
     * @param role нова роль
     */
    public static void updateCurrentUserRole(String role) {
        if (currentUser != null) {
            currentUser.setRole(role);
        }
    }

    /**
     * Скидає сесію (вихід).
     */
    public static void clear() {
        currentUser = null;
    }

    /**
     * Перевіряє, чи користувач авторизований.
     *
     * @return true, якщо є користувач
     */
    public static boolean isAuthenticated() {
        return currentUser != null;
    }
}
