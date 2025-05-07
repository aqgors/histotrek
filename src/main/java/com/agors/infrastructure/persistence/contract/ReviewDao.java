package com.agors.infrastructure.persistence.contract;

import com.agors.domain.entity.Review;
import java.util.List;

public interface ReviewDao {
    Review add(Review review);
    List<Review> findByPlace(int placeId);
    List<Review> findByUser(int userId);
    void remove(int reviewId);
}
