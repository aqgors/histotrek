package com.agors.infrastructure.persistence.impl;

import com.agors.domain.entity.Report;
import com.agors.infrastructure.persistence.contract.ReportDao;
import com.agors.infrastructure.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реалізація інтерфейсу ReportDao для роботи з таблицею report.
 * <p>
 * Забезпечує додавання, отримання та видалення об'єктів Report
 * у базі даних за допомогою JDBC.</p>
 *
 * @author agors
 * @version 1.0
 */
public class ReportDaoImpl implements ReportDao {

    /**
     * Додає новий звіт до таблиці report.
     *
     * @param report об'єкт Report з даними для збереження
     * @return збережений об'єкт Report з встановленим id
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public Report add(Report report) {
        String sql = "INSERT INTO report (type, generated_at, content) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, report.getType());
            stmt.setTimestamp(2, Timestamp.valueOf(report.getGeneratedAt()));
            stmt.setString(3, report.getContent());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    report.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося додати звіт: " + report, e);
        }
        return report;
    }

    /**
     * Повертає список усіх звітів з таблиці report.
     *
     * @return список об'єктів Report
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public List<Report> findAll() {
        String sql = "SELECT id, type, generated_at, content FROM report";
        List<Report> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToReport(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося завантажити звіти", e);
        }
        return list;
    }

    /**
     * Знаходить звіт за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор звіту
     * @return знайдений об'єкт Report або null, якщо не знайдено
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public Report findById(int id) {
        String sql = "SELECT id, type, generated_at, content FROM report WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReport(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося знайти звіт з id=" + id, e);
        }
        return null;
    }

    /**
     * Видаляє звіт за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор звіту
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public void remove(int id) {
        String sql = "DELETE FROM report WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося видалити звіт з id=" + id, e);
        }
    }

    /**
     * Допоміжний метод для мапінгу поточного рядка ResultSet в об'єкт Report.
     *
     * @param rs ResultSet з полями id, type, generated_at, content
     * @return екземпляр Report з даними з рядка
     * @throws SQLException у разі помилки доступу до полів ResultSet
     */
    private Report mapRowToReport(ResultSet rs) throws SQLException {
        Report r = new Report();
        r.setId(rs.getInt("id"));
        r.setType(rs.getString("type"));
        r.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
        r.setContent(rs.getString("content"));
        return r;
    }
}
