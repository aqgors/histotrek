package com.agors.infrastructure.persistence.impl;

import com.agors.domain.entity.Review;
import com.agors.infrastructure.persistence.contract.ReviewDao;
import com.agors.infrastructure.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDaoImpl implements ReviewDao {

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

    @Override
    public void update(Review review) {
        String sql = "UPDATE review SET text = ?, rating = ?, created_at = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, review.getText());
            stmt.setInt(2, review.getRating());
            stmt.setTimestamp(3, Timestamp.valueOf(review.getCreatedAt()));
            stmt.setInt(4, review.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося оновити відгук: " + review, e);
        }
    }

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
