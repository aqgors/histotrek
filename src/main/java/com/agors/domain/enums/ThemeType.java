package com.agors.domain.enums;

/**
 * Перелік доступних тем оформлення інтерфейсу в застосунку Histotrek.
 * <p>
 * Теми впливають на кольори фону, тексту, елементів керування та піщаної анімації.
 * Вибір теми зберігається між сесіями користувача.
 * </p>
 *
 * <ul>
 *   <li>{@code DEFAULT} — Головна тема з градієнтним фоном (пісочно-оранжева)</li>
 *   <li>{@code LIGHT} — Світла тема з білим фоном</li>
 *   <li>{@code DARK} — Темна тема з темно-сірим фоном</li>
 * </ul>
 *
 * @author agors
 * @version 1.0
 */
public enum ThemeType {

    /** Основна (дефолтна) тема з теплим градієнтом. */
    DEFAULT,

    /** Світла тема з білим інтерфейсом. */
    LIGHT,

    /** Темна тема для роботи в нічний час або для зниження навантаження на очі. */
    DARK
}
