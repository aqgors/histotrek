package com.agors.infrastructure.persistence.impl;

import com.agors.domain.entity.User;
import com.agors.infrastructure.util.ConnectionHolder;
import com.agors.infrastructure.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SessionDaoImpl {

    private static final String INSERT_SESSION = """
        INSERT INTO user_session (user_id, session_token, is_active)
        VALUES (?, ?, 1)
        """;

    public void createSession(int userId, String token) {
        try {
            Connection conn = ConnectionManager.getConnection();
            ConnectionHolder.setConnection(conn);
            try (PreparedStatement ps = conn.prepareStatement(INSERT_SESSION)) {
                ps.setInt(1, userId);
                ps.setString(2, token);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка створення сесії", e);
        } finally {
            ConnectionHolder.clearConnection();
        }
    }

    public void deactivateByUserId(int userId) {
        String sql = "UPDATE user_session SET is_active = 0 WHERE user_id = ?";
        try {
            Connection conn = ConnectionManager.getConnection();
            ConnectionHolder.setConnection(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка завершення сесії", e);
        } finally {
            ConnectionHolder.clearConnection();
        }
    }

    public Optional<User> findUserByActiveSession() {
        String sql = """
            SELECT TOP 1 u.id, u.username, u.email, u.password, u.role
            FROM users u
            JOIN user_session s ON s.user_id = u.id
            WHERE s.is_active = 1
            ORDER BY s.login_time DESC
        """;

        try {
            Connection conn = ConnectionManager.getConnection();
            ConnectionHolder.setConnection(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setPasswordHash(rs.getString("password"));
                    u.setRole(rs.getString("role"));
                    return Optional.of(u);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка пошуку активної сесії", e);
        } finally {
            ConnectionHolder.clearConnection();
        }

        return Optional.empty();
    }

    public void deleteByUserId(int userId) {
        String sql = "DELETE FROM user_session WHERE user_id = ?";
        try {
            Connection conn = ConnectionManager.getConnection();
            ConnectionHolder.setConnection(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення сесії", e);
        } finally {
            ConnectionHolder.clearConnection();
        }
    }
}
