package com.agors.infrastructure.persistence.impl;

import com.agors.domain.entity.Favorite;
import com.agors.infrastructure.persistence.contract.FavoriteDao;
import com.agors.infrastructure.util.ConnectionHolder;
import com.agors.infrastructure.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реалізація інтерфейсу FavoriteDao для роботи з таблицею favorite.
 * <p>
 * Забезпечує додавання, отримання та видалення записів обраних місць користувача
 * у базі даних за допомогою JDBC.</p>
 *
 * @author agors
 * @version 1.0
 */
public class FavoriteDaoImpl implements FavoriteDao {

    /**
     * Додає новий запис обраного місця у таблицю favorite.
     *
     * @param fav об'єкт Favorite з інформацією про користувача та місце
     * @return збережений об'єкт Favorite з встановленим id
     * @throws RuntimeException у разі помилки доступу до БД
     */
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
            throw new RuntimeException("Не вдалося додати улюблене місце: " + fav, e);
        }
        return fav;
    }

    /**
     * Повертає список обраних місць для вказаного користувача.
     *
     * @param userId унікальний ідентифікатор користувача
     * @return список об'єктів Favorite
     * @throws RuntimeException у разі помилки доступу до БД
     */
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
            throw new RuntimeException("Не вдалося отримати улюблені місця для користувача: " + userId, e);
        }
        return list;
    }

    /**
     * Видаляє запис обраного місця для користувача.
     *
     * @param userId  унікальний ідентифікатор користувача
     * @param placeId унікальний ідентифікатор місця
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public void remove(int userId, int placeId) {
        String sql = "DELETE FROM favorite WHERE user_id = ? AND place_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, placeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося видалити обране місце: користувач=" + userId + ", місце=" + placeId, e);
        }
    }

    /**
     * Допоміжний метод для мапінгу поточного рядка ResultSet в об'єкт Favorite.
     *
     * @param rs ResultSet з полями id, user_id, place_id, created_at
     * @return екземпляр Favorite з даними
     * @throws SQLException у разі помилки доступу до полів ResultSet
     */
    private Favorite mapRowToFavorite(ResultSet rs) throws SQLException {
        Favorite f = new Favorite();
        f.setId(rs.getInt("id"));
        f.setUserId(rs.getInt("user_id"));
        f.setPlaceId(rs.getInt("place_id"));
        f.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return f;
    }

    public boolean isFavorite(int userId, int placeId) {
        String sql = "SELECT TOP 1 1 FROM favorite WHERE user_id = ? AND place_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, placeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося перевірити обране місце", e);
        }
    }

    public void addToFavorites(int userId, int placeId) {
        String sqlCheck = "SELECT 1 FROM favorite WHERE user_id = ? AND place_id = ?";
        String sqlInsert = "INSERT INTO favorite (user_id, place_id, created_at) VALUES (?, ?, GETDATE())";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck)) {

            stmtCheck.setInt(1, userId);
            stmtCheck.setInt(2, placeId);

            try (ResultSet rs = stmtCheck.executeQuery()) {
                if (!rs.next()) {
                    try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                        stmtInsert.setInt(1, userId);
                        stmtInsert.setInt(2, placeId);
                        stmtInsert.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при додаванні до обраного", e);
        }
    }
}
