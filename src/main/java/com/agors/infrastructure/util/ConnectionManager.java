package com.agors.infrastructure.util;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionManager {

    private static final String URL_KEY       = "db.url";
    private static final String USERNAME_KEY  = "db.username";
    private static final String PASSWORD_KEY  = "db.password";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final int DEFAULT_SIZE     = 5;

    private static final BlockingQueue<Connection> pool;
    private static final List<Connection> realConnections = new ArrayList<>();

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

    private static void loadDriver() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC Driver for SQL Server not found", e);
        }
    }

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

    private static Connection openNew() {
        try {
            String url = PropertiesUtil.get(URL_KEY);
            String usr = PropertiesUtil.get(USERNAME_KEY);
            String pwd = PropertiesUtil.get(PASSWORD_KEY);
            if (url == null || usr == null || pwd == null) {
                throw new IllegalStateException("Azure SQL properties not set");
            }
            return DriverManager.getConnection(url, usr, pwd);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot open Azure SQL DB connection", e);
        }
    }

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

    public static void shutdown() {
        for (Connection real : realConnections) {
            try {
                real.close();
            } catch (SQLException ignored) {}
        }
    }
}
