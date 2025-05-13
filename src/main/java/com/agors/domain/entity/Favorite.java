package com.agors.domain.entity;

import java.time.LocalDateTime;

/**
 * Сутність, що представляє обране місце користувача.
 * <p>
 * Включає ідентифікатори запису, користувача та місця,
 * а також дату й час створення цього запису.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class Favorite {

    /** Унікальний ідентифікатор запису */
    private int id;
    /** Ідентифікатор користувача, який додав місце у вибране */
    private int userId;
    /** Ідентифікатор місця, доданого у вибране */
    private int placeId;
    /** Дата та час створення запису */
    private LocalDateTime createdAt;

    /**
     * Повертає унікальний ідентифікатор запису.
     *
     * @return ідентифікатор запису
     */
    public int getId() {
        return id;
    }

    /**
     * Встановлює унікальний ідентифікатор запису.
     *
     * @param id ідентифікатор запису
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Повертає ідентифікатор користувача.
     *
     * @return ідентифікатор користувача
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Встановлює ідентифікатор користувача.
     *
     * @param userId ідентифікатор користувача
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Повертає ідентифікатор місця.
     *
     * @return ідентифікатор місця
     */
    public int getPlaceId() {
        return placeId;
    }

    /**
     * Встановлює ідентифікатор місця.
     *
     * @param placeId ідентифікатор місця
     */
    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    /**
     * Повертає дату та час створення запису.
     *
     * @return дата та час створення
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Встановлює дату та час створення запису.
     *
     * @param createdAt дата та час створення запису
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}