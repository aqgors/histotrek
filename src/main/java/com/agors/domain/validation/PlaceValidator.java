package com.agors.domain.validation;

import com.agors.domain.entity.Place;
import com.agors.infrastructure.util.I18n;

import java.util.HashMap;
import java.util.Map;

/**
 * Валідатор для об'єкта {@link Place}, який перевіряє коректність введених даних.
 * <p>
 * Перевіряє обов'язкові поля: назву, країну, епоху, опис і URL зображення.
 * Також виконує базову перевірку формату URL.
 * Підтримує локалізовані повідомлення про помилки через {@link I18n}.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class PlaceValidator {

    /**
     * Перевіряє переданий об'єкт {@link Place} на валідність.
     *
     * @param place об'єкт для перевірки
     * @return мапа помилок, де ключ — імʼя поля, значення — повідомлення про помилку
     */
    public Map<String, String> validate(Place place) {
        Map<String, String> errors = new HashMap<>();

        if (isBlank(place.getName())) {
            errors.put("name", I18n.get("error_name_required", "Назва місця не може бути порожньою."));
        }
        if (isBlank(place.getCountry())) {
            errors.put("country", I18n.get("error_country_required", "Країна не може бути порожньою."));
        }
        if (isBlank(place.getEra())) {
            errors.put("era", I18n.get("error_era_required", "Епоха не може бути порожньою."));
        }
        if (isBlank(place.getDescription())) {
            errors.put("description", I18n.get("error_description_required", "Опис не може бути порожнім."));
        }
        if (isBlank(place.getImageUrl())) {
            errors.put("imageUrl", I18n.get("error_image_url_required", "URL зображення не може бути порожнім."));
        } else if (!place.getImageUrl().matches("^(http|https)://.*")) {
            errors.put("imageUrl", I18n.get("error_image_url_invalid", "URL повинен починатись з http:// або https://."));
        }

        return errors;
    }

    /**
     * Перевіряє, чи рядок є порожнім або містить лише пробіли.
     *
     * @param value вхідне значення
     * @return true, якщо рядок порожній або null
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
