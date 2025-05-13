package com.agors.domain.entity;

import java.time.LocalDateTime;

/**
 * Сутність звіту в системі.
 * <p>
 * Містить інформацію про унікальний ідентифікатор,
 * тип звіту, час його генерації та вміст.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class Report {

    /** Унікальний ідентифікатор звіту */
    private int id;
    /** Тип звіту (наприклад, "PDF", "HTML"). */
    private String type;
    /** Час генерації звіту */
    private LocalDateTime generatedAt;
    /** Вміст звіту у вигляді тексту або JSON */
    private String content;

    /**
     * Повертає унікальний ідентифікатор звіту.
     *
     * @return ідентифікатор звіту
     */
    public int getId() {
        return id;
    }

    /**
     * Встановлює унікальний ідентифікатор звіту.
     *
     * @param id ідентифікатор звіту
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Повертає тип звіту.
     *
     * @return тип звіту
     */
    public String getType() {
        return type;
    }

    /**
     * Встановлює тип звіту.
     *
     * @param type тип звіту
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Повертає час генерації звіту.
     *
     * @return час генерації
     */
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    /**
     * Встановлює час генерації звіту.
     *
     * @param generatedAt час генерації звіту
     */
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    /**
     * Повертає вміст звіту.
     *
     * @return текстовий вміст звіту
     */
    public String getContent() {
        return content;
    }

    /**
     * Встановлює вміст звіту.
     *
     * @param content текстовий вміст звіту
     */
    public void setContent(String content) {
        this.content = content;
    }
}