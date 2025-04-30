package com.agors.application.window;

import javafx.animation.*;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreen {

    public void show(Stage primaryStage) {
        Text title = new Text("HISTOTREK");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Impact", 80));
        title.setOpacity(0);
        title.setScaleX(0.1);
        title.setScaleY(0.1);

        StackPane root = new StackPane(title);
        root.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(root, 800, 450);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), title);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(2), title);
        scaleUp.setFromX(0.1);
        scaleUp.setFromY(0.1);
        scaleUp.setToX(1);
        scaleUp.setToY(1);

        ParallelTransition appear = new ParallelTransition(fadeIn, scaleUp);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), title);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition sequence = new SequentialTransition(appear, pause, fadeOut);
        sequence.setOnFinished(event -> {
            showMainScene(primaryStage);
        });

        primaryStage.setScene(scene);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreen(true);
        primaryStage.show();
        sequence.play();
    }

    private void showMainScene(Stage stage) {
        Text mainText = new Text("Головне вікно");
        mainText.setFill(Color.WHITE);
        mainText.setFont(Font.font(40));
        StackPane mainRoot = new StackPane(mainText);
        mainRoot.setStyle("-fx-background-color: darkslategray;");
        Scene mainScene = new Scene(mainRoot, 800, 450);

        stage.setScene(mainScene);
        stage.setFullScreen(true);
    }
}