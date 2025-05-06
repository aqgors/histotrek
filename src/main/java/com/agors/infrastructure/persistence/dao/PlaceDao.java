package com.agors.infrastructure.persistence.dao;

import com.agors.domain.entity.Place;
import com.agors.infrastructure.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaceDao {

    /** Повертає всі місця */
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
            throw new RuntimeException("Error loading all places", e);
        }
        return places;
    }

    /** Повертає одне місце за ID або null */
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
            throw new RuntimeException("Error finding place by id=" + id, e);
        }
        return null;
    }

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