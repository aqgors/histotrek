package com.agors.domain.entity;

import com.agors.domain.enums.ReportType;
import java.time.LocalDateTime;

/**
 * Сутність {@code Report} представляє звіт, сформований у системі Histotrek.
 * <p>
 * Звіт може містити дані про користувачів, історичні місця, статистику тощо,
 * збережені у вигляді тексту, та має тип, дату створення й унікальний ідентифікатор.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class Report {

    /** Унікальний ідентифікатор звіту. */
    private int id;

    /** Тип звіту (наприклад, статистичний, текстовий експорт тощо). */
    private ReportType type;

    /** Дата й час генерації звіту. */
    private LocalDateTime generatedAt;

    /** Вміст звіту у вигляді тексту (HTML, Markdown або plain text). */
    private String content;

    /**
     * Повертає ідентифікатор звіту.
     *
     * @return id звіту
     */
    public int getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор звіту.
     *
     * @param id унікальний ідентифікатор
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Повертає тип звіту.
     *
     * @return тип {@link ReportType}
     */
    public ReportType getType() {
        return type;
    }

    /**
     * Встановлює тип звіту.
     *
     * @param type тип звіту
     */
    public void setType(ReportType type) {
        this.type = type;
    }

    /**
     * Повертає дату та час створення звіту.
     *
     * @return дата й час {@link LocalDateTime}
     */
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    /**
     * Встановлює дату та час створення звіту.
     *
     * @param generatedAt момент генерації
     */
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    /**
     * Повертає текстовий вміст звіту.
     *
     * @return вміст у вигляді рядка
     */
    public String getContent() {
        return content;
    }

    /**
     * Встановлює текстовий вміст звіту.
     *
     * @param content вміст звіту
     */
    public void setContent(String content) {
        this.content = content;
    }
}
