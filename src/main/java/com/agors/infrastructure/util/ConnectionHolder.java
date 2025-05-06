// src/main/java/com/agors/infrastructure/persistence/util/ConnectionHolder.java
package com.agors.infrastructure.util;

import java.sql.Connection;

/**
 * Утримувач з'єднання для забезпечення використання одного з'єднання в межах потоку/транзакції.
 * Використовує ThreadLocal.
 */
public class ConnectionHolder {
    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    /** Прив'язує з'єднання до поточного потоку */
    public static void setConnection(Connection connection) {
        connectionHolder.set(connection);
    }

    /** Повертає з'єднання, прив'язане до поточного потоку (або null) */
    public static Connection getConnection() {
        return connectionHolder.get();
    }

    /** Звільняє прив'язане до потоку з'єднання */
    public static void clearConnection() {
        connectionHolder.remove();
    }
}
