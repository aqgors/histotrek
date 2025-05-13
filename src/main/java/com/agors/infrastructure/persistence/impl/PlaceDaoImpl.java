package com.agors.infrastructure.persistence.impl;

import com.agors.domain.entity.Place;
import com.agors.infrastructure.persistence.contract.PlaceDao;
import com.agors.infrastructure.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реалізація інтерфейсу PlaceDao для роботи з таблицею place.
 * <p>
 * Забезпечує додавання, отримання, оновлення та видалення об'єктів Place
 * у базі даних за допомогою JDBC.</p>
 *
 * @author agors
 * @version 1.0
 */
public class PlaceDaoImpl implements PlaceDao {

    /**
     * Додає новий запис місця до таблиці place.
     *
     * @param place об'єкт Place з інформацією про місце
     * @return збережений об'єкт Place з встановленим id
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public Place add(Place place) {
        String sql = "INSERT INTO place (name, country, era, description, image_url) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, place.getName());
            stmt.setString(2, place.getCountry());
            stmt.setString(3, place.getEra());
            stmt.setString(4, place.getDescription());
            stmt.setString(5, place.getImageUrl());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    place.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося додати місце: " + place, e);
        }
        return place;
    }

    /**
     * Повертає список усіх місць із таблиці place.
     *
     * @return список об'єктів Place
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public List<Place> findAll() {
        List<Place> places = new ArrayList<>();
        String sql = "SELECT id, name, country, era, description, image_url FROM place";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                places.add(mapRowToPlace(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося завантажити місця", e);
        }
        return places;
    }

    /**
     * Знаходить місце за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор місця
     * @return знайдений об'єкт Place або null, якщо не знайдено
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public Place findById(int id) {
        String sql = "SELECT id, name, country, era, description, image_url FROM place WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPlace(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося знайти місце з id=" + id, e);
        }
        return null;
    }

    /**
     * Оновлює дані існуючого місця у таблиці place.
     *
     * @param place об'єкт Place з оновленими даними
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public void update(Place place) {
        String sql = "UPDATE place SET name = ?, country = ?, era = ?, description = ?, image_url = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, place.getName());
            stmt.setString(2, place.getCountry());
            stmt.setString(3, place.getEra());
            stmt.setString(4, place.getDescription());
            stmt.setString(5, place.getImageUrl());
            stmt.setInt(6, place.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося оновити місце: " + place, e);
        }
    }

    /**
     * Видаляє місце за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор місця
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public void remove(int id) {
        String sql = "DELETE FROM place WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося видалити місце з id=" + id, e);
        }
    }

    /**
     * Допоміжний метод для мапінгу поточного рядка ResultSet в об'єкт Place.
     *
     * @param rs ResultSet з полями id, name, country, era, description, image_url
     * @return екземпляр Place з даними з рядка
     * @throws SQLException у разі помилки доступу до полів ResultSet
     */
    private Place mapRowToPlace(ResultSet rs) throws SQLException {
        Place p = new Place();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setCountry(rs.getString("country"));
        p.setEra(rs.getString("era"));
        p.setDescription(rs.getString("description"));
        p.setImageUrl(rs.getString("image_url"));
        return p;
    }
}
