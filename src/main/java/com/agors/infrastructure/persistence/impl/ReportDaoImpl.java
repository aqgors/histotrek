package com.agors.infrastructure.persistence.impl;

import com.agors.domain.entity.Report;
import com.agors.domain.enums.ReportType;
import com.agors.infrastructure.persistence.contract.ReportDao;
import com.agors.infrastructure.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реалізація інтерфейсу {@link ReportDao} для роботи з таблицею report.
 * <p>
 * Забезпечує додавання, отримання, пошук та видалення звітів у базі даних за допомогою JDBC.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class ReportDaoImpl implements ReportDao {

    /**
     * Додає новий звіт до таблиці report.
     *
     * @param report об'єкт {@link Report}, який необхідно зберегти
     * @return збережений об'єкт {@link Report} з встановленим ID
     * @throws RuntimeException якщо виникає помилка при з'єднанні з базою або виконанні SQL-запиту
     */
    @Override
    public Report add(Report report) {
        String sql = "INSERT INTO report (type, generated_at, content) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, report.getType().name());
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
     * Повертає список усіх звітів з бази даних.
     *
     * @return список об'єктів {@link Report}
     * @throws RuntimeException якщо виникає помилка при з'єднанні з базою або виконанні SQL-запиту
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
     * Знаходить звіт за його ID.
     *
     * @param id ідентифікатор звіту
     * @return об'єкт {@link Report} або {@code null}, якщо звіт не знайдено
     * @throws RuntimeException якщо виникає помилка при з'єднанні з базою або виконанні SQL-запиту
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
     * Видаляє звіт з бази даних за його ID.
     *
     * @param id ідентифікатор звіту
     * @throws RuntimeException якщо виникає помилка при з'єднанні з базою або виконанні SQL-запиту
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
     * Перетворює поточний рядок з {@link ResultSet} у об'єкт {@link Report}.
     *
     * @param rs результат SQL-запиту
     * @return заповнений об'єкт {@link Report}
     * @throws SQLException якщо виникає помилка при читанні даних з ResultSet
     */
    private Report mapRowToReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setId(rs.getInt("id"));
        report.setType(ReportType.valueOf(rs.getString("type")));
        report.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
        report.setContent(rs.getString("content"));
        return report;
    }
}