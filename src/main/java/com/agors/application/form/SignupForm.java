package com.agors.application.form;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SignupForm {

    public void show(Stage stage) {
        // Головний контейнер
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f4f4;");

        Label titleLabel = new Label("Створіть акаунт");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.web("#2a2a2a"));

        // Поля введення
        TextField usernameField = new TextField();
        usernameField.setPromptText("Ім'я користувача");
        styleField(usernameField);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        styleField(emailField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");
        styleField(passwordField);

        // Кнопка Реєстрації
        Button signupButton = new Button("Зареєструватися");
        signupButton.setPrefWidth(200);
        signupButton.setStyle("-fx-background-color: #c2b280; -fx-text-fill: white; -fx-font-size: 14px;");
        signupButton.setOnAction(e -> {
            // Тут можна додати логіку перевірки і збереження
            System.out.println("Користувач зареєстрований: " + usernameField.getText());
            showAlert("Успіх!", "Реєстрація пройшла успішно!");
        });

        // Кнопка Назад
        Button backButton = new Button("Назад");
        backButton.setPrefWidth(200);
        backButton.setStyle("-fx-background-color: transparent; -fx-border-color: #c2b280; -fx-text-fill: #2a2a2a;");
        backButton.setOnAction(e -> {
            // Повернення до головного вікна
            HistotrekApp app = new HistotrekApp();
            app.start(stage);
        });

        root.getChildren().addAll(titleLabel, usernameField, emailField, passwordField, signupButton, backButton);

        Scene scene = new Scene(root, 400, 500);
        stage.setScene(scene);
        stage.setTitle("Реєстрація - Histotrek");
        stage.show();
    }

    private void styleField(TextField field) {
        field.setPrefWidth(300);
        field.setPrefHeight(40);
        field.setStyle("-fx-font-size: 14px; -fx-border-color: #d3d3d3;");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
