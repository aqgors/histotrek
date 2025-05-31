package com.agors.infrastructure.util;

import com.agors.domain.enums.ThemeType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * Клас {@code ThemeManager} відповідає за управління темою інтерфейсу користувача у застосунку Histotrek.
 * <p>
 * Дозволяє зчитувати, змінювати, застосовувати та зберігати обрану тему в локальному файлі конфігурації.
 * Також підтримує підписку на зміну теми через слухачів (listeners).
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class ThemeManager {

    /** Назва конфігураційного файлу з темою */
    private static final String FILE = "theme.properties";

    /** Поточна активна тема */
    private static ThemeType currentTheme = loadTheme();

    /** Список слухачів, яких потрібно повідомити при зміні теми */
    private static final List<Consumer<ThemeType>> listeners = new ArrayList<>();

    /**
     * Повертає поточну активну тему.
     *
     * @return {@link ThemeType} активної теми
     */
    public static ThemeType getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Встановлює нову тему, зберігає її у файл та повідомляє слухачів.
     *
     * @param theme нова тема для застосунку
     */
    public static void setTheme(ThemeType theme) {
        if (theme != currentTheme) {
            currentTheme = theme;
            saveTheme(theme);
            notifyListeners();
        }
    }

    /**
     * Застосовує поточну тему до вказаної сцени.
     *
     * @param scene сцена, до якої застосовується тема
     */
    public static void applyTheme(Scene scene) {
        if (scene != null && scene.getRoot() != null) {
            applyTheme(scene.getRoot(), currentTheme);
        }
    }

    /**
     * Застосовує вказану тему до кореневого елемента інтерфейсу.
     *
     * @param root         кореневий елемент
     * @param themeToApply тема, яку слід застосувати
     */
    public static void applyTheme(Parent root, ThemeType themeToApply) {
        if (root != null) {
            String style = switch (themeToApply) {
                case DARK -> "-fx-background-color: #3c3f41;";
                case LIGHT -> "-fx-background-color: white;";
                case DEFAULT -> "-fx-background-color: linear-gradient(to bottom right, #fdf6e3, #e29264);";
            };
            root.setStyle(style);
        }
    }

    /**
     * Повертає набір кольорів для анімації піску (використовуються у splash screen).
     *
     * @return масив з двома кольорами: чорним і білим
     */
    public static Color[] getSandColors() {
        return new Color[]{Color.BLACK, Color.WHITE};
    }

    /**
     * Додає слухача для відстеження зміни теми.
     *
     * @param listener функція, яка буде викликана при зміні теми
     */
    public static void addThemeChangeListener(Consumer<ThemeType> listener) {
        listeners.add(listener);
    }

    /**
     * Повідомляє всіх зареєстрованих слухачів про зміну теми.
     */
    private static void notifyListeners() {
        for (Consumer<ThemeType> listener : listeners) {
            listener.accept(currentTheme);
        }
    }

    /**
     * Завантажує тему з конфігураційного файлу.
     *
     * @return {@link ThemeType} збереженої теми або DEFAULT за замовчуванням
     */
    private static ThemeType loadTheme() {
        try (InputStream in = new FileInputStream(FILE)) {
            Properties props = new Properties();
            props.load(in);
            String themeStr = props.getProperty("theme", "DEFAULT").trim().toUpperCase();
            for (ThemeType type : ThemeType.values()) {
                if (type.name().equalsIgnoreCase(themeStr)) {
                    return type;
                }
            }
        } catch (Exception ignored) {
        }
        return ThemeType.DEFAULT;
    }

    /**
     * Зберігає вибрану тему у файл {@code theme.properties}.
     *
     * @param theme тема, яку потрібно зберегти
     */
    private static void saveTheme(ThemeType theme) {
        try (OutputStream out = new FileOutputStream(FILE)) {
            Properties props = new Properties();
            props.setProperty("theme", theme.name());
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
