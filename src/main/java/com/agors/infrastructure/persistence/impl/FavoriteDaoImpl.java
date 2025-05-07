package com.agors.infrastructure.persistence.impl;

import com.agors.domain.entity.Favorite;
import com.agors.infrastructure.persistence.contract.FavoriteDao;
import com.agors.infrastructure.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDaoImpl implements FavoriteDao {

    @Override
    public Favorite add(Favorite fav) {
        String sql = "INSERT INTO favorite (user_id, place_id, created_at) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, fav.getUserId());
            stmt.setInt(2, fav.getPlaceId());
            stmt.setTimestamp(3, Timestamp.valueOf(fav.getCreatedAt()));
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    fav.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting favorite: " + fav, e);
        }
        return fav;
    }

    @Override
    public List<Favorite> findByUser(int userId) {
        String sql = "SELECT id, user_id, place_id, created_at FROM favorite WHERE user_id = ?";
        List<Favorite> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToFavorite(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching favorites for user " + userId, e);
        }
        return list;
    }

    @Override
    public void remove(int userId, int placeId) {
        String sql = "DELETE FROM favorite WHERE user_id = ? AND place_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, placeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting favorite: user=" + userId + ", place=" + placeId, e);
        }
    }

    private Favorite mapRowToFavorite(ResultSet rs) throws SQLException {
        Favorite f = new Favorite();
        f.setId(rs.getInt("id"));
        f.setUserId(rs.getInt("user_id"));
        f.setPlaceId(rs.getInt("place_id"));
        f.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return f;
    }
}