package com.agors.infrastructure.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    /**
     * Повертає значення за ключем або null, якщо ключ не знайдено
     */
    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    /**
     * Повертає значення за ключем або defaultValue, якщо ключ не знайдено
     */
    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    private static void loadProperties() {
        try (InputStream in = PropertiesUtil.class.getClassLoader()
            .getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new RuntimeException("application.properties not found in classpath");
            }
            PROPERTIES.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Error loading application.properties", e);
        }
    }

    private PropertiesUtil() {}
}
