package com.agors.application.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBoxTest {

    @Test
    void testExists() {
        assertDoesNotThrow(() -> {
            assertNotNull(MessageBox.class.getDeclaredMethod("show", String.class, String.class, javafx.stage.Stage.class));
            assertNotNull(MessageBox.class.getDeclaredMethod("showConfirm", String.class, String.class, javafx.stage.Stage.class));
        });
    }
}