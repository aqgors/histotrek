package com.agors;

import com.agors.application.ui.SplashScreen;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Головний клас JavaFX-застосунку Histotrek.
 * <p>
 * Цей клас є точкою входу в програму,
 * ініціалізує та відображає екран завантаження.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class HistotrekMain extends Application {

    /**
     * Запускає JavaFX-застосунок, відображаючи екран завантаження.
     *
     * @param primaryStage головна сцена застосунку
     */
    @Override
    public void start(Stage primaryStage) {
        SplashScreen splash = new SplashScreen();
        splash.show(primaryStage);
    }

    /**
     * Головний метод, що запускає JavaFX-застосунок.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        launch(args);
    }
}
