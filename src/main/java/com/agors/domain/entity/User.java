package com.agors.domain.entity;

/**
 * Сутність користувача системи.
 * <p>
 * Містить дані про ім'я користувача, email, захешований пароль та роль.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class User {

    /** Унікальний ідентифікатор користувача */
    private int id;
    /** Логін користувача */
    private String username;
    /** Електронна пошта користувача */
    private String email;
    /** Захешований пароль користувача */
    private String passwordHash;
    /** Роль користувача в системі */
    private String role;

    /**
     * Повертає унікальний ідентифікатор користувача.
     * @return ідентифікатор користувача
     */
    public int getId() {
        return id;
    }

    /**
     * Встановлює унікальний ідентифікатор користувача.
     * @param id ідентифікатор користувача
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Повертає логін користувача.
     * @return логін користувача
     */
    public String getUsername() {
        return username;
    }

    /**
     * Встановлює логін користувача.
     * @param username логін користувача
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Повертає електронну пошту користувача.
     * @return email користувача
     */
    public String getEmail() {
        return email;
    }

    /**
     * Встановлює електронну пошту користувача.
     * @param email email користувача
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Повертає захешований пароль користувача.
     * @return хеш пароля
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Встановлює захешований пароль користувача.
     * @param passwordHash хеш пароля
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Повертає роль користувача.
     * @return роль користувача
     */
    public String getRole() {
        return role;
    }

    /**
     * Встановлює роль користувача.
     * @param role роль користувача
     */
    public void setRole(String role) {
        this.role = role;
    }
}
