package com.agors.application.form;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginForm {

    public void show(Stage stage, Stage previousStage) {
        VBox formBox = createFormLayout();

        Text titleLabel = createTitle("Log in");
        TextField userOrEmailField = createField("Username or Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleField(passwordField);

        Button loginButton = createMainButton("Log in");
        loginButton.setOnAction(e -> {
            System.out.println("Logging in with: " + userOrEmailField.getText());
            showAlert("Info", "Вхід виконано (поки що заглушка)");
        });

        Button backButton = createBorderedButton("Back");
        backButton.setOnAction(e -> {
            stage.close();
            previousStage.show();
        });

        VBox fields = new VBox(15, userOrEmailField, passwordField, loginButton, backButton);
        fields.setAlignment(Pos.CENTER);
        formBox.getChildren().addAll(titleLabel, fields);

        Pane animationLayer = new Pane();
        animationLayer.setMouseTransparent(true);
        playSandAnimation(animationLayer);

        StackPane root = new StackPane(animationLayer, formBox);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #fdf6e3, #e29264);");

        Scene scene = new Scene(root, 800, 600);
        animationLayer.prefWidthProperty().bind(scene.widthProperty());
        animationLayer.prefHeightProperty().bind(scene.heightProperty());

        stage.setScene(scene);
        stage.setTitle("Histotrek");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
        root.requestFocus();
    }

    private VBox createFormLayout() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(40));
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private Text createTitle(String text) {
        Text title = new Text(text);
        title.setFont(Font.font("Arial", 32));
        title.setFill(Color.web("#3e2723"));
        title.setEffect(new DropShadow(3, Color.rgb(0, 0, 0, 0.15)));
        return title;
    }

    private TextField createField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        styleField(field);
        return field;
    }

    private void styleField(TextField field) {
        field.setMaxWidth(320);
        field.setPrefHeight(45);
        field.setFont(Font.font("Arial", 14));
        field.setStyle("-fx-background-color: rgba(255, 255, 255, 0.6); -fx-border-color: #d3d3d3; " +
            "-fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 0 10 0 10;");
    }

    private Button createMainButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(320, 50);
        button.setFont(Font.font("Arial", 16));
        button.setStyle("-fx-background-color: #c2b280; -fx-text-fill: white; -fx-background-radius: 12;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #a99e75; -fx-text-fill: white; -fx-background-radius: 12;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #c2b280; -fx-text-fill: white; -fx-background-radius: 12;"));
        button.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));
        return button;
    }

    private Button createBorderedButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(320, 50);
        button.setFont(Font.font("Arial", 16));
        button.setStyle("-fx-background-color: transparent; -fx-border-color: #c2b280; -fx-text-fill: #3e2723; " +
            "-fx-background-radius: 12; -fx-border-radius: 12;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #f5e4c4; -fx-border-color: #a99e75; -fx-text-fill: #3e2723; -fx-background-radius: 12; -fx-border-radius: 12;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-border-color: #c2b280; -fx-text-fill: #3e2723; -fx-background-radius: 12; -fx-border-radius: 12;"));
        button.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));
        return button;
    }

    private void playSandAnimation(Pane pane) {
        Timeline sandTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            double width = pane.getWidth();
            double height = pane.getHeight();

            Circle sand = new Circle(2, Color.web("#000000", 0.4));
            sand.setCenterX(Math.random() * width);
            sand.setCenterY(height);

            pane.getChildren().add(sand);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(3), sand);
            tt.setByY(-height);
            tt.setByX(Math.random() * 60 - 30);
            tt.setOnFinished(ev -> pane.getChildren().remove(sand));
            tt.play();
        }));
        sandTimeline.setCycleCount(Timeline.INDEFINITE);
        sandTimeline.setRate(1.2);
        sandTimeline.play();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
