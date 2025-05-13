package com.agors.domain.entity;

import java.time.LocalDateTime;

/**
 * Сутність відгуку користувача про місце.
 * <p>
 * Містить інформацію про текст відгуку, оцінку, пов'язаний користувач та місце,
 * а також дату створення відгуку.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class Review {

    /** Унікальний ідентифікатор відгуку */
    private int id;
    /** Ідентифікатор місця, до якого прив'язано відгук */
    private int placeId;
    /** Ідентифікатор користувача, який залишив відгук */
    private int userId;
    /** Текстовий вміст відгуку */
    private String text;
    /** Оцінка місця (наприклад, від 1 до 5) */
    private int rating;
    /** Дата та час створення відгуку */
    private LocalDateTime createdAt;

    /**
     * Повертає унікальний ідентифікатор відгуку.
     *
     * @return ідентифікатор відгуку
     */
    public int getId() {
        return id;
    }

    /**
     * Встановлює унікальний ідентифікатор відгуку.
     *
     * @param id ідентифікатор відгуку
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Повертає ідентифікатор місця, до якого прив'язано відгук.
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
     * Повертає текст відгуку.
     *
     * @return текст відгуку
     */
    public String getText() {
        return text;
    }

    /**
     * Встановлює текст відгуку.
     *
     * @param text текст відгуку
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Повертає оцінку місця.
     *
     * @return оцінка від 1 до 5
     */
    public int getRating() {
        return rating;
    }

    /**
     * Встановлює оцінку місця.
     *
     * @param rating оцінка від 1 до 5
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Повертає дату та час створення відгуку.
     *
     * @return дата та час створення
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Встановлює дату та час створення відгуку.
     *
     * @param createdAt дата та час створення
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}