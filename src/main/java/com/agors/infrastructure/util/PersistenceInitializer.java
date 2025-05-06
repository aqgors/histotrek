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
 * Ініціалізатор БД: виконує DDL та DML скрипти з ресурсів.
 * Викликати вручну на старті програми.
 */
public class PersistenceInitializer {

    private static final String DDL_PATH = "ddl_postgresql.sql";
    private static final String DML_PATH = "dml_postgresql.sql";

    /** Виконує створення схеми та наповнення даними */
    public static void init() {
        try (Connection conn = ConnectionManager.getConnection();
            Statement stmt  = conn.createStatement()) {

            conn.setAutoCommit(false);
            stmt.execute(readSql(DDL_PATH));
            boolean runDml = Boolean.parseBoolean(PropertiesUtil.get("db.run.dml", "true"));
            if (runDml) {
                stmt.execute(readSql(DML_PATH));
            }
            conn.commit();

        } catch (SQLException e) {
            throw new RuntimeException("DB initialization failed", e);
        }
    }

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
            throw new RuntimeException("Cannot read SQL resource: " + resource, e);
        }
    }
}