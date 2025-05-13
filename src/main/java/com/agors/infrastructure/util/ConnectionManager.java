package com.agors.infrastructure.util;

import com.agors.infrastructure.util.PropertiesUtil;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Менеджер пулу з'єднань до бази даних.
 * <p>
 * Ініціалізує фіксований пул з'єднань на основі налаштувань із властивостей,
 * повертає проксі-з'єднання, яке при виклику close() повертає об'єкт у пул,
 * а не закриває реальне з'єднання.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class ConnectionManager {

    private static final String URL_KEY       = "db.url";
    private static final String USERNAME_KEY  = "db.username";
    private static final String PASSWORD_KEY  = "db.password";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final int    DEFAULT_SIZE  = 5;

    /** Черга доступних проксі-з'єднань */
    private static final BlockingQueue<Connection> pool;
    /** Список реальних з'єднань для закриття при завершенні */
    private static final List<Connection>          realConnections = new ArrayList<>();

    static {
        loadDriver();
        int size = DEFAULT_SIZE;
        String cfg = PropertiesUtil.get(POOL_SIZE_KEY);
        if (cfg != null) {
            try { size = Integer.parseInt(cfg); } catch (NumberFormatException ignored) {}
        }
        pool = new ArrayBlockingQueue<>(size);
        initPool(size);
    }

    private ConnectionManager() {}

    /**
     * Завантажує JDBC-драйвер.
     * @throws RuntimeException якщо драйвер не знайдено
     */
    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC Driver not found", e);
        }
    }

    /**
     * Ініціалізує пул з'єднань заданого розміру.
     * @param size кількість з'єднань у пулі
     */
    private static void initPool(int size) {
        for (int i = 0; i < size; i++) {
            Connection real = openNew();
            Connection proxy = (Connection) Proxy.newProxyInstance(
                ConnectionManager.class.getClassLoader(),
                new Class[]{ Connection.class },
                (proxyConn, method, args) -> {
                    if ("close".equals(method.getName())) {
                        pool.offer((Connection) proxyConn);
                        ConnectionHolder.clearConnection();
                        return null;
                    } else {
                        return method.invoke(real, args);
                    }
                }
            );
            pool.offer(proxy);
            realConnections.add(real);
        }
    }

    /**
     * Відкриває нове реальне з'єднання за налаштуваннями з властивостей.
     * @return екземпляр Connection
     * @throws RuntimeException у разі помилки підключення
     */
    private static Connection openNew() {
        try {
            String url  = PropertiesUtil.get(URL_KEY);
            String usr  = PropertiesUtil.get(USERNAME_KEY);
            String pwd  = PropertiesUtil.get(PASSWORD_KEY);
            if (url == null || usr == null || pwd == null) {
                throw new IllegalStateException("DB properties not set");
            }
            return DriverManager.getConnection(url, usr, pwd);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot open DB connection", e);
        }
    }

    /**
     * Повертає проксі-з'єднання з пулу та прив'язує його до поточного потоку.
     * @return екземпляр Connection
     * @throws RuntimeException якщо процес очікування перервано
     */
    public static Connection getConnection() {
        try {
            Connection conn = pool.take();
            ConnectionHolder.setConnection(conn);
            return conn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for DB connection", e);
        }
    }

    /**
     * Закриває всі реальні з'єднання при завершенні програми.
     */
    public static void shutdown() {
        for (Connection real : realConnections) {
            try { real.close(); }
            catch (SQLException e) { /* логувати помилку */ }
        }
    }
}