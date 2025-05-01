package com.agors.domain.dao;

import com.agors.domain.entity.Place;
import com.agors.infrastructure.utill.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaceDao {

    public List<Place> findAll() {
        List<Place> places = new ArrayList<>();

        String sql = "SELECT id, name, country, era, description, image_url FROM place";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Place place = new Place();
                place.setId(rs.getInt("id"));
                place.setName(rs.getString("name"));
                place.setCountry(rs.getString("country"));
                place.setEra(rs.getString("era"));
                place.setDescription(rs.getString("description"));
                place.setImageUrl(rs.getString("image_url"));

                places.add(place);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return places;
    }
}
