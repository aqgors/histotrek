package com.agors.infrastructure.persistence.contract;

import com.agors.domain.entity.Place;
import java.util.List;

public interface PlaceDao {
    Place add(Place place);
    List<Place> findAll();
    Place findById(int id);
    void update(Place place);
    void remove(int id);
}
