package com.agors.infrastructure.persistence.impl;

import com.agors.domain.entity.Report;
import com.agors.infrastructure.persistence.contract.ReportDao;
import com.agors.infrastructure.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDaoImpl implements ReportDao {

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
            throw new RuntimeException("Error inserting report: " + report, e);
        }
        return report;
    }

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
            throw new RuntimeException("Error loading all reports", e);
        }
        return list;
    }

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
            throw new RuntimeException("Error finding report id=" + id, e);
        }
        return null;
    }

    @Override
    public void remove(int id) {
        String sql = "DELETE FROM report WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting report id=" + id, e);
        }
    }

    private Report mapRowToReport(ResultSet rs) throws SQLException {
        Report r = new Report();
        r.setId(rs.getInt("id"));
        r.setType(rs.getString("type"));
        r.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
        r.setContent(rs.getString("content"));
        return r;
    }
}