package com.agors.infrastructure.persistence.contract;

import com.agors.domain.entity.Review;
import java.util.List;

/**
 * Контракт для роботи з відгуками користувачів у базі даних.
 * <p>
 * Оголошує методи для додавання, пошуку та видалення об'єктів Review.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public interface ReviewDao {

    /**
     * Додає новий відгук до бази даних.
     *
     * @param review об'єкт Review з даними відгуку
     * @return збережений об'єкт Review з встановленим id
     */
    Review add(Review review);

    /**
     * Повертає список відгуків для зазначеного місця.
     *
     * @param placeId унікальний ідентифікатор місця
     * @return список об'єктів Review
     */
    List<Review> findByPlace(int placeId);

    /**
     * Повертає список відгуків, залишених зазначеним користувачем.
     *
     * @param userId унікальний ідентифікатор користувача
     * @return список об'єктів Review
     */
    List<Review> findByUser(int userId);

    /**
     * Видаляє відгук за його унікальним ідентифікатором.
     *
     * @param reviewId унікальний ідентифікатор відгуку
     */
    void remove(int reviewId);

    void update(Review review);
}
