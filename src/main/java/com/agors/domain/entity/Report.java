package com.agors.domain.entity;

import java.time.LocalDateTime;

public class Report {
    private int id;
    private String type;
    private LocalDateTime generatedAt;
    private String content;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
