package com.agors.infrastructure.persistence.contract;

import com.agors.domain.entity.User;
import java.util.List;

/**
 * Контракт для роботи з користувачами в базі даних.
 * <p>
 * Оголошує методи для додавання, отримання, оновлення та видалення користувачів.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public interface UserDao {

    /**
     * Додає нового користувача до бази даних.
     *
     * @param user об'єкт User з даними для збереження
     */
    void addUser(User user);

    /**
     * Повертає користувача за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор користувача
     * @return знайдений об'єкт User або null, якщо не знайдено
     */
    User getUserById(int id);

    /**
     * Шукає користувача за логіном або електронною поштою.
     *
     * @param loginOrEmail логін або email користувача
     * @return знайдений об'єкт User або null, якщо не знайдено
     */
    User getByUsernameOrEmail(String loginOrEmail);

    /**
     * Повертає список усіх користувачів.
     *
     * @return список об'єктів User
     */
    List<User> getAllUsers();

    /**
     * Оновлює дані існуючого користувача.
     *
     * @param user об'єкт User з оновленими даними
     */
    void updateUser(User user);

    /**
     * Видаляє користувача за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор користувача
     */
    void deleteUser(int id);
}
