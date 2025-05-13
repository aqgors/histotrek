package com.agors.infrastructure.persistence.contract;

import com.agors.domain.entity.Place;
import java.util.List;

/**
 * Контракт для роботи з історичними місцями в базі даних.
 * <p>
 * Оголошує методи для додавання, отримання, оновлення та видалення об'єктів Place.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public interface PlaceDao {

    /**
     * Додає нове місце до бази даних.
     *
     * @param place об'єкт Place з даними для збереження
     * @return збережений об'єкт Place з встановленим id
     */
    Place add(Place place);

    /**
     * Повертає список усіх місць.
     *
     * @return список об'єктів Place
     */
    List<Place> findAll();

    /**
     * Знаходить місце за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор місця
     * @return знайдений об'єкт Place або null, якщо не знайдено
     */
    Place findById(int id);

    /**
     * Оновлює дані існуючого місця.
     *
     * @param place об'єкт Place з оновленими даними
     */
    void update(Place place);

    /**
     * Видаляє місце за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор місця
     */
    void remove(int id);
}