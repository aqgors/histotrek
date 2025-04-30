package com.agors.application.window;

import com.agors.application.form.HistotrekApp;
import javafx.animation.*;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreen {

    public void show(Stage stage) {
        Text title = new Text("HISTOTREK");
        title.setFill(Color.web("#c2b280")); // Вінтажний стиль
        title.setFont(Font.font("Times New Roman", 80));
        title.setOpacity(0);
        title.setTranslateY(-50);

        StackPane root = new StackPane(title);
        root.setStyle("-fx-background-color: #2a2a2a;");
        Scene scene = new Scene(root, 800, 600);

        // Анімації: fade + move
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), title);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition moveDown = new TranslateTransition(Duration.seconds(2), title);
        moveDown.setFromY(-50);
        moveDown.setToY(0);

        ParallelTransition showTitle = new ParallelTransition(fadeIn, moveDown);

        showTitle.setOnFinished(e -> transitionToMain(stage));

        stage.setScene(scene);
        stage.show();
        showTitle.play();
    }

    private void transitionToMain(Stage stage) {
        // Плавний fade out Splash
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), stage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            // Після fade out запускаємо головну сцену
            HistotrekApp app = new HistotrekApp();
            app.start(stage);
        });
        fadeOut.play();
    }
}
