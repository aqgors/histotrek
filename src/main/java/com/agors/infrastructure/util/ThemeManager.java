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

public class ThemeManager {

    private static final String FILE = "theme.properties";
    private static ThemeType currentTheme = loadTheme();
    private static final List<Consumer<ThemeType>> listeners = new ArrayList<>();

    public static ThemeType getCurrentTheme() {
        return currentTheme;
    }

    public static void setTheme(ThemeType theme) {
        if (theme != currentTheme) {
            currentTheme = theme;
            saveTheme(theme);
            notifyListeners();
        }
    }

    public static void applyTheme(Scene scene) {
        if (scene != null && scene.getRoot() != null) {
            applyTheme(scene.getRoot(), currentTheme);
        }
    }

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

    public static Color[] getSandColors() {
        return new Color[]{Color.BLACK, Color.WHITE};
    }

    public static void addThemeChangeListener(Consumer<ThemeType> listener) {
        listeners.add(listener);
    }

    private static void notifyListeners() {
        for (Consumer<ThemeType> listener : listeners) {
            listener.accept(currentTheme);
        }
    }

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
