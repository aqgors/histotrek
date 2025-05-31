package com.agors.infrastructure.util;

import java.sql.Connection;

/**
 * Утилітний клас для зберігання та доступу до з'єднання з базою даних,
 * яке пов'язане з поточним потоком.
 * <p>
 * Цей клас використовується для передачі {@link java.sql.Connection} між DAO-класами в межах одного запиту або транзакції.
 * Допомагає уникнути передачі з'єднання через параметри методів.
 * </p>
 *
 * <h3>Приклад використання:</h3>
 * <pre>{@code
 * Connection conn = ConnectionManager.getConnection();
 * ConnectionHolder.setConnection(conn);
 * // DAO-класи отримують з'єднання через ConnectionHolder.getConnection()
 * ConnectionHolder.clearConnection();
 * }</pre>
 *
 * <strong>⚠ Увага:</strong> Ця реалізація використовує єдине поле {@code static Connection},
 * тобто вона не є безпечною для багатопотокового середовища.
 * Для справжньої потокобезпеки слід використовувати {@code ThreadLocal<Connection>}.
 *
 * @author agors
 * @version 1.0
 */
public class ConnectionHolder {
    /**
     * Поточне з'єднання з базою даних, що використовується в межах поточного контексту.
     */
    private static Connection connection;

    /**
     * Встановлює з'єднання, яке буде доступне через {@link #getConnection()}.
     *
     * @param conn з'єднання з базою даних
     */
    public static void setConnection(Connection conn) {
        connection = conn;
    }

    /**
     * Повертає поточне збережене з'єднання.
     *
     * @return з'єднання з базою даних
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * Очищає збережене з'єднання.
     * Викликається після завершення операцій з базою.
     */
    public static void clearConnection() {
        connection = null;
    }
}
