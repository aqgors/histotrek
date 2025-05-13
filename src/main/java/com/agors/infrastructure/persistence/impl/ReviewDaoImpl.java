package com.agors.infrastructure.persistence.impl;

import com.agors.domain.entity.Review;
import com.agors.infrastructure.persistence.contract.ReviewDao;
import com.agors.infrastructure.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реалізація інтерфейсу ReviewDao для роботи з таблицею review.
 * <p>
 * Забезпечує додавання, отримання та видалення відгуків
 * у базі даних за допомогою JDBC.</p>
 *
 * @author agors
 * @version 1.0
 */
public class ReviewDaoImpl implements ReviewDao {

    /**
     * Додає новий відгук до таблиці review.
     *
     * @param review об'єкт Review з даними відгуку
     * @return збережений екземпляр Review з встановленим id
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public Review add(Review review) {
        String sql = "INSERT INTO review (place_id, user_id, text, rating, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, review.getPlaceId());
            stmt.setInt(2, review.getUserId());
            stmt.setString(3, review.getText());
            stmt.setInt(4, review.getRating());
            stmt.setTimestamp(5, Timestamp.valueOf(review.getCreatedAt()));
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    review.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося додати відгук: " + review, e);
        }
        return review;
    }

    /**
     * Повертає список відгуків для вказаного місця.
     *
     * @param placeId унікальний ідентифікатор місця
     * @return список екземплярів Review
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public List<Review> findByPlace(int placeId) {
        String sql = "SELECT id, place_id, user_id, text, rating, created_at FROM review WHERE place_id = ?";
        List<Review> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, placeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToReview(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося отримати відгуки для місця: " + placeId, e);
        }
        return list;
    }

    /**
     * Повертає список відгуків, залишених вказаним користувачем.
     *
     * @param userId унікальний ідентифікатор користувача
     * @return список екземплярів Review
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public List<Review> findByUser(int userId) {
        String sql = "SELECT id, place_id, user_id, text, rating, created_at FROM review WHERE user_id = ?";
        List<Review> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToReview(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося отримати відгуки для користувача: " + userId, e);
        }
        return list;
    }

    /**
     * Видаляє відгук за його ідентифікатором.
     *
     * @param reviewId унікальний ідентифікатор відгуку
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public void remove(int reviewId) {
        String sql = "DELETE FROM review WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reviewId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося видалити відгук з id=" + reviewId, e);
        }
    }

    /**
     * Допоміжний метод для мапінгу ResultSet в об'єкт Review.
     *
     * @param rs ResultSet з полями id, place_id, user_id, text, rating, created_at
     * @return екземпляр Review з даними з поточного рядка
     * @throws SQLException у разі помилки доступу до ResultSet
     */
    private Review mapRowToReview(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getInt("id"));
        r.setPlaceId(rs.getInt("place_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setText(rs.getString("text"));
        r.setRating(rs.getInt("rating"));
        r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return r;
    }
}
