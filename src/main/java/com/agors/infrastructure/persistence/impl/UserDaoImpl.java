package com.agors.infrastructure.persistence.impl;

import com.agors.domain.entity.User;
import com.agors.infrastructure.persistence.contract.UserDao;
import com.agors.infrastructure.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реалізація інтерфейсу UserDao для роботи з таблицею users.
 * <p>
 * Забезпечує додавання, отримання, оновлення та видалення користувачів
 * у базі даних за допомогою JDBC.</p>
 *
 * @author agors
 * @version 1.0
 */
public class UserDaoImpl implements UserDao {

    /**
     * Додає нового користувача до таблиці users.
     *
     * @param user об'єкт User з даними для збереження
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося додати користувача: " + user, e);
        }
    }

    /**
     * Знаходить користувача за унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор користувача
     * @return знайдений об'єкт User або null, якщо не знайдено
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося знайти користувача з id=" + id, e);
        }
        return null;
    }

    /**
     * Шукає користувача за логіном або електронною поштою.
     *
     * @param loginOrEmail логін або email користувача
     * @return знайдений об'єкт User або null, якщо не знайдено
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public User getByUsernameOrEmail(String loginOrEmail) {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, loginOrEmail);
            stmt.setString(2, loginOrEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося знайти користувача за логіном/email=" + loginOrEmail, e);
        }
        return null;
    }

    /**
     * Повертає список усіх користувачів.
     *
     * @return список об'єктів User
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося завантажити користувачів", e);
        }
        return users;
    }

    /**
     * Оновлює дані існуючого користувача.
     *
     * @param user об'єкт User з оновленими даними
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public void updateUser(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, password = ?, role = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole());
            stmt.setInt(5, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося оновити користувача: " + user, e);
        }
    }

    /**
     * Видаляє користувача за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор користувача
     * @throws RuntimeException у разі помилки доступу до БД
     */
    @Override
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося видалити користувача з id=" + id, e);
        }
    }

    /**
     * Допоміжний метод для мапінгу ResultSet в об'єкт User.
     *
     * @param rs ResultSet з полями id, username, email, password, role
     * @return екземпляр User з даними з поточного рядка
     * @throws SQLException у разі помилки доступу до ResultSet
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password"));
        user.setRole(rs.getString("role"));
        return user;
    }

    /**
     * Перевіряє, чи існує користувач з вказаним іменем користувача в таблиці users.
     *
     * @param username ім’я користувача для перевірки
     * @return {@code true}, якщо користувач з таким іменем існує; {@code false} — інакше
     * @throws RuntimeException у разі помилки доступу до бази даних
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося перевірити ім’я користувача: " + username, e);
        }
    }

    /**
     * Перевіряє, чи існує користувач з вказаною електронною поштою в таблиці users.
     *
     * @param email електронна пошта для перевірки
     * @return {@code true}, якщо користувач з такою поштою існує; {@code false} — інакше
     * @throws RuntimeException у разі помилки доступу до бази даних
     */
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося перевірити email: " + email, e);
        }
    }
}
