package com.agors.infrastructure.persistence.contract;

import com.agors.domain.entity.Favorite;
import java.util.List;

public interface FavoriteDao {
    Favorite add(Favorite fav);
    List<Favorite> findByUser(int userId);
    void remove(int userId, int placeId);
}
