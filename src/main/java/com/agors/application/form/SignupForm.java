package com.agors.application.form;

import com.agors.domain.dao.UserDao;
import com.agors.domain.entity.User;
import com.agors.domain.validation.SignupValidator;
import com.agors.infrastructure.util.PasswordUtil;
import com.agors.infrastructure.message.MessageBox;

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

import java.util.Map;

public class SignupForm {

    public void show(Stage stage, Stage previousStage) {
        VBox formBox = new VBox(20);
        formBox.setPadding(new Insets(40));
        formBox.setAlignment(Pos.CENTER);

        Text titleLabel = new Text("Sign up");
        titleLabel.setFont(Font.font("Arial", 32));
        titleLabel.setFill(Color.web("#3e2723"));
        titleLabel.setEffect(new DropShadow(3, Color.rgb(0, 0, 0, 0.15)));

        TextField usernameField = createField("Username");
        Label usernameError = createErrorLabel();

        TextField emailField = createField("Email");
        Label emailError = createErrorLabel();

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleField(passwordField);
        Label passwordError = createErrorLabel();

        Button signupButton = createMainButton("Create an account");
        signupButton.setOnAction(e -> {
            Map<String, String> errors = SignupValidator.validate(
                usernameField.getText(),
                emailField.getText(),
                passwordField.getText()
            );

            usernameError.setText(errors.getOrDefault("username", ""));
            emailError.setText(errors.getOrDefault("email", ""));
            passwordError.setText(errors.getOrDefault("password", ""));

            if (errors.isEmpty()) {
                User user = new User();
                user.setUsername(usernameField.getText());
                user.setEmail(emailField.getText());
                user.setPasswordHash(PasswordUtil.hashPassword(passwordField.getText()));
                user.setRole("USER");

                new UserDao().addUser(user);
                MessageBox.show("Success", "Registration was successful!");

                usernameField.clear();
                emailField.clear();
                passwordField.clear();
            }
        });


        Button backButton = createBorderedButton("Back");
        backButton.setOnAction(e -> {
            stage.hide();
            previousStage.show();
        });

        VBox fields = new VBox(10,
            usernameField, usernameError,
            emailField, emailError,
            passwordField, passwordError,
            signupButton, backButton
        );
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

    private Label createErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", 12));
        return errorLabel;
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
        button.setStyle("-fx-background-color: transparent; -fx-border-color: #c2b280; -fx-text-fill: #3e2723; -fx-background-radius: 12; -fx-border-radius: 12;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #f5e4c4; -fx-border-color: #a99e75; -fx-text-fill: #3e2723; -fx-background-radius: 12; -fx-border-radius: 12;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-border-color: #c2b280; -fx-text-fill: #3e2723; -fx-background-radius: 12; -fx-border-radius: 12;"));
        button.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));
        return button;
    }

    private void playSandAnimation(Pane sandPane) {
        Timeline sandTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            double width = sandPane.getWidth();
            double height = sandPane.getHeight();

            Circle sand = new Circle(2, Color.web("#000000", 0.4));
            sand.setCenterX(Math.random() * width);
            sand.setCenterY(height);

            sandPane.getChildren().add(sand);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(3), sand);
            tt.setByY(-height);
            tt.setByX(Math.random() * 60 - 30);
            tt.setOnFinished(ev -> sandPane.getChildren().remove(sand));
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