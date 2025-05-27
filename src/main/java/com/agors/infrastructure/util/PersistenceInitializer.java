package com.agors.infrastructure.util;

import com.agors.infrastructure.util.PropertiesUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Ініціалізатор бази даних для застосунку.
 * <p>
 * Виконує DDL та за потреби DML скрипти з ресурсів для створення схеми
 * та початкового заповнення даними. Викликати на старті програми.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class PersistenceInitializer {

    private static final String DDL_PATH = "db/ddl_postgresql.sql";
    private static final String DML_PATH = "db/dml_postgresql.sql";

    /**
     * Виконує ініціалізацію бази даних: створює схему та наповнює дані.
     * <p>
     * Встановлює закриття транзакції вручну, виконує DDL,
     * а якщо налаштовано, виконує DML, потім комітить.
     * </p>
     *
     * @throws RuntimeException у разі помилки SQL або читання ресурсів
     */
    public static void init() {
        try {
            Connection conn = ConnectionManager.getConnection();
            ConnectionHolder.setConnection(conn); // ⬅️ зберігаємо зʼєднання для DAO

            try (Statement stmt = conn.createStatement()) {
                conn.setAutoCommit(false);
                stmt.execute(readSql(DDL_PATH));
                boolean runDml = Boolean.parseBoolean(PropertiesUtil.get("db.run.dml", "true"));
                if (runDml) {
                    stmt.execute(readSql(DML_PATH));
                }
                conn.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ініціалізація БД не вдалася", e);
        }
    }

    /**
     * Зчитує SQL-скрипт з ресурсу та повертає як рядок.
     *
     * @param resource ім'я файлу ресурсу в клас-шляху
     * @return вміст SQL-файлу як рядок
     * @throws RuntimeException у разі помилки читання
     */
    private static String readSql(String resource) {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                Objects.requireNonNull(
                    PersistenceInitializer.class
                        .getClassLoader()
                        .getResourceAsStream(resource)
                )
            )
        )) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Не вдалося прочитати ресурс SQL: " + resource, e);
        }
    }
}