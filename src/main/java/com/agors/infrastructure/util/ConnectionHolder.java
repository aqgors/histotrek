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
    private static Connection connection;

    public static void setConnection(Connection conn) {
        connection = conn;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void clearConnection() {
        connection = null;
    }
}
