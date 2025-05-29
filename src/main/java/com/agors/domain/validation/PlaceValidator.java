package com.agors.domain.validation;

import com.agors.domain.entity.Place;
import com.agors.infrastructure.util.I18n;

import java.util.HashMap;
import java.util.Map;

public class PlaceValidator {

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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
