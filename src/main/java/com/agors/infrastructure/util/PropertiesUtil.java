package com.agors.infrastructure.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Утилітний клас для завантаження та доступу до властивостей з файлу application.properties.
 * <p>
 * Завантажує властивості при ініціалізації класу та надає методи отримання значень за ключем.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class PropertiesUtil {

    /** Об'єкт Properties для зберігання завантажених властивостей */
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    /**
     * Повертає значення властивості за ключем.
     *
     * @param key ключ властивості
     * @return значення властивості або null, якщо ключ не знайдено
     */
    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    /**
     * Повертає значення властивості за ключем або значення за замовчуванням, якщо ключ не знайдено.
     *
     * @param key          ключ властивості
     * @param defaultValue значення за замовчуванням
     * @return значення властивості або defaultValue, якщо ключ не знайдено
     */
    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    /**
     * Завантажує властивості з файлу application.properties у клас-шляху.
     *
     * @throws RuntimeException якщо файл не знайдено або виникла помилка читання
     */
    private static void loadProperties() {
        try (InputStream in = PropertiesUtil.class.getClassLoader()
            .getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new RuntimeException("application.properties не знайдено в classpath");
            }
            PROPERTIES.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Помилка завантаження application.properties", e);
        }
    }

    /**
     * Приватний конструктор для заборони створення екземплярів.
     */
    private PropertiesUtil() {}
}