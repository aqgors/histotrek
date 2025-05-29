package com.agors.domain.validation;

import com.agors.domain.entity.Place;

import java.util.HashMap;
import java.util.Map;

public class PlaceValidator {

    public Map<String, String> validate(Place place) {
        Map<String, String> errors = new HashMap<>();

        if (isBlank(place.getName())) {
            errors.put("name", "Назва місця не може бути порожньою.");
        }
        if (isBlank(place.getCountry())) {
            errors.put("country", "Країна не може бути порожньою.");
        }
        if (isBlank(place.getEra())) {
            errors.put("era", "Епоха не може бути порожньою.");
        }
        if (isBlank(place.getDescription())) {
            errors.put("description", "Опис не може бути порожнім.");
        }
        if (isBlank(place.getImageUrl())) {
            errors.put("imageUrl", "URL зображення не може бути порожнім.");
        } else if (!place.getImageUrl().matches("^(http|https)://.*")) {
            errors.put("imageUrl", "URL повинен починатись з http:// або https://.");
        }

        return errors;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
