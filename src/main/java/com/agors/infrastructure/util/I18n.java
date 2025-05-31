package com.agors.infrastructure.util;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * Клас I18n відповідає за інтернаціоналізацію (локалізацію) інтерфейсу користувача.
 * <p>
 * Зчитує мовні ресурси з файлу <code>i18n/messages_*.properties</code> і дозволяє
 * змінювати поточну мову, зберігаючи її в {@link Preferences}.
 * </p>
 *
 * <p>Підтримується зберігання обраної мови між сесіями користувача.</p>
 *
 * Приклад використання:
 * <pre>{@code
 *     String label = I18n.get("login_button");
 * }</pre>
 *
 * @author agors
 * @version 1.0
 */
public class I18n {

    /** Зберігає налаштування користувача для мови */
    private static final Preferences prefs = Preferences.userNodeForPackage(I18n.class);

    /** Ключ для збереження мови у preferences */
    private static final String LANG_KEY = "app_language";

    /** Поточна мова локалі */
    private static Locale currentLocale;

    /** Поточний набір локалізованих ресурсів */
    private static ResourceBundle bundle;

    // Ініціалізація мови при старті програми
    static {
        String savedLang = prefs.get(LANG_KEY, "uk");
        setLanguage(savedLang);
    }

    /**
     * Встановлює поточну мову інтерфейсу за кодом ISO (наприклад, "uk", "en").
     *
     * @param langCode мовний код, наприклад "en" або "uk"
     */
    public static void setLanguage(String langCode) {
        currentLocale = new Locale(langCode);
        prefs.put(LANG_KEY, langCode);
        bundle = ResourceBundle.getBundle("i18n.messages", currentLocale);
    }

    /**
     * Повертає значення з ресурсного файлу за ключем.
     *
     * @param key ключ (наприклад, "login_button")
     * @return локалізоване значення
     * @throws java.util.MissingResourceException якщо ключ не знайдено
     */
    public static String get(String key) {
        return bundle.getString(key);
    }

    /**
     * Повертає значення з ресурсного файлу за ключем, або значення за замовчуванням.
     *
     * @param key ключ (наприклад, "login_button")
     * @param defaultValue значення за замовчуванням, якщо ключ не знайдено
     * @return локалізоване значення або defaultValue
     */
    public static String get(String key, String defaultValue) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Повертає поточну {@link Locale}, що використовується в інтерфейсі.
     *
     * @return поточна локаль
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Повертає локалізоване значення за ключем або сам текст, якщо ключ не знайдено.
     * <p>
     * Зручно для UI, де може бути передано як ключ, так і готовий текст.
     * </p>
     *
     * @param keyOrText ключ або текст
     * @return локалізований текст або передане значення
     */
    public static String getOrDefault(String keyOrText) {
        if (keyOrText == null) return "";
        try {
            return bundle.getString(keyOrText);
        } catch (Exception e) {
            return keyOrText;
        }
    }
}
