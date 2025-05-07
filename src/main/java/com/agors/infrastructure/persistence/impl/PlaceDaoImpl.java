package com.agors.infrastructure.persistence.impl;

import com.agors.domain.entity.Place;
import com.agors.infrastructure.persistence.contract.PlaceDao;
import com.agors.infrastructure.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaceDaoImpl implements PlaceDao {

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
            throw new RuntimeException("Error inserting place: " + place, e);
        }
        return place;
    }

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
            throw new RuntimeException("Error loading all places", e);
        }
        return places;
    }

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
            throw new RuntimeException("Error finding place by id=" + id, e);
        }
        return null;
    }

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
            throw new RuntimeException("Error updating place: " + place, e);
        }
    }

    @Override
    public void remove(int id) {
        String sql = "DELETE FROM place WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting place id=" + id, e);
        }
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