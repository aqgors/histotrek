package com.agors.domain.validation;

import com.agors.domain.entity.Place;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlaceValidatorTest {

    @Test
    void testValidateWithEmptyFields() {
        Place place = new Place();
        PlaceValidator validator = new PlaceValidator();

        Map<String, String> errors = validator.validate(place);

        assertFalse(errors.isEmpty());
        assertTrue(errors.containsKey("name"));
        assertTrue(errors.containsKey("country"));
        assertTrue(errors.containsKey("era"));
        assertTrue(errors.containsKey("description"));
        assertTrue(errors.containsKey("imageUrl"));
    }

    @Test
    void testValidateWithValidData() {
        Place place = new Place();
        place.setName("Colosseum");
        place.setCountry("Italy");
        place.setEra("Roman Empire");
        place.setDescription("Famous amphitheatre in Rome.");
        place.setImageUrl("http://example.com/image.jpg");

        PlaceValidator validator = new PlaceValidator();
        Map<String, String> errors = validator.validate(place);

        assertTrue(errors.isEmpty());
    }
}
