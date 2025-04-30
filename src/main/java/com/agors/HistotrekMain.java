package com.agors;

import com.agors.application.window.SplashScreen;
import javafx.application.Application;
import javafx.stage.Stage;

public class HistotrekMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        SplashScreen splash = new SplashScreen();
        splash.show(primaryStage);
    }

    public static void main(String[] args) {
        launch();
    }
}