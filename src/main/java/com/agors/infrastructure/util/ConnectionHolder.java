package com.agors.infrastructure.util;

import java.sql.Connection;

/**
 * Утилітний клас для зберігання та доступу до одного з'єднання з базою
 * даних на потік за допомогою ThreadLocal.
 * <p>
 * Дозволяє прив'язати з'єднання до поточного потоку/транзакції,
 * отримати його та звільнити після використання.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class ConnectionHolder {
    /** ThreadLocal для зберігання з'єднання на потік */
    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    /**
     * Прив'язує з'єднання до поточного потоку.
     *
     * @param connection екземпляр Connection для встановлення
     */
    public static void setConnection(Connection connection) {
        connectionHolder.set(connection);
    }

    /**
     * Повертає з'єднання, прив'язане до поточного потоку.
     *
     * @return екземпляр Connection або null, якщо не встановлено
     */
    public static Connection getConnection() {
        return connectionHolder.get();
    }

    /**
     * Звільняє з'єднання, прив'язане до поточного потоку.
     */
    public static void clearConnection() {
        connectionHolder.remove();
    }
}
