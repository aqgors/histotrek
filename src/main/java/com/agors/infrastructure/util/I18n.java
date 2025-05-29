package com.agors.infrastructure.util;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class I18n {
    private static final Preferences prefs = Preferences.userNodeForPackage(I18n.class);
    private static final String LANG_KEY = "app_language";

    private static Locale currentLocale;
    private static ResourceBundle bundle;

    static {
        String savedLang = prefs.get(LANG_KEY, "uk");
        setLanguage(savedLang);
    }

    public static void setLanguage(String langCode) {
        currentLocale = new Locale(langCode);
        prefs.put(LANG_KEY, langCode);
        bundle = ResourceBundle.getBundle("i18n.messages", currentLocale);
    }

    public static String get(String key) {
        return bundle.getString(key);
    }

    public static String get(String key, String defaultValue) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static String getOrDefault(String keyOrText) {
        if (keyOrText == null) return "";
        try {
            return bundle.getString(keyOrText);
        } catch (Exception e) {
            return keyOrText;
        }
    }
}
