package com.agors.infrastructure.persistence.contract;

import com.agors.domain.entity.Favorite;
import java.util.List;

/**
 * Контракт для роботи з обраними місцями користувача.
 * <p>
 * Визначає методи для додавання, пошуку та видалення записів обраного.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public interface FavoriteDao {

    /**
     * Додає новий запис обраного місця для користувача.
     *
     * @param fav екземпляр Favorite з даними про користувача та місце
     * @return збережений екземпляр Favorite з встановленим id
     */
    Favorite add(Favorite fav);

    /**
     * Повертає список обраних місць для заданого користувача.
     *
     * @param userId унікальний ідентифікатор користувача
     * @return список Favorite для користувача
     */
    List<Favorite> findByUser(int userId);

    /**
     * Видаляє запис обраного місця для користувача.
     *
     * @param userId  унікальний ідентифікатор користувача
     * @param placeId унікальний ідентифікатор місця
     */
    void remove(int userId, int placeId);
}