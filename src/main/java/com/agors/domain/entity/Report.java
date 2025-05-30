package com.agors.domain.entity;

import com.agors.domain.enums.ReportType;
import java.time.LocalDateTime;

/**
 * Сутність звіту в системі.
 */
public class Report {

    /** Унікальний ідентифікатор звіту */
    private int id;

    /** Тип звіту */
    private ReportType type;

    /** Час генерації звіту */
    private LocalDateTime generatedAt;

    /** Вміст звіту */
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
