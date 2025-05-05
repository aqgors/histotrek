package com.agors.application.form;

import com.agors.domain.entity.User;
import com.agors.domain.validation.LoginValidator;
import com.agors.infrastructure.util.PasswordUtil;
import com.agors.domain.dao.UserDao;
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

public class LoginForm {

    public void show(Stage stage, Stage previousStage) {
        VBox formBox = createFormLayout();

        Text titleLabel = createTitle("Log in");

        TextField loginField = createField("Username or Email");
        Label loginError = createErrorLabel();

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleField(passwordField);
        Label passwordError = createErrorLabel();

        Button loginButton = createMainButton("Log in");
        loginButton.setOnAction(e -> {

            loginError.setText("");
            passwordError.setText("");

            Map<String, String> errors = LoginValidator.validate(
                loginField.getText(),
                passwordField.getText()
            );

            loginError.setText(errors.getOrDefault("login", ""));
            passwordError.setText(errors.getOrDefault("password", ""));

            if (errors.isEmpty()) {

                User user = new UserDao()
                    .getByUsernameOrEmail(loginField.getText());
                MessageBox.show("Success", "Welcome, " + user.getUsername() + "!");
                stage.close();
                previousStage.show();
            }
        });

        Button backButton = createBorderedButton("Back");
        backButton.setOnAction(e -> {
            stage.close();
            previousStage.show();
        });

        VBox fields = new VBox(8,
            loginField, loginError,
            passwordField, passwordError,
            loginButton, backButton
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
        field.setStyle(
            "-fx-background-color: rgba(255,255,255,0.6); " +
                "-fx-border-color: #d3d3d3; " +
                "-fx-background-radius: 8; -fx-border-radius: 8; " +
                "-fx-padding: 0 10;"
        );
    }

    private Label createErrorLabel() {
        Label lbl = new Label();
        lbl.setTextFill(Color.RED);
        lbl.setFont(Font.font("Arial", 12));
        lbl.setMaxWidth(320);
        lbl.setWrapText(true);
        return lbl;
    }

    private Button createMainButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(320, 50);
        btn.setFont(Font.font("Arial", 16));
        btn.setStyle("-fx-background-color: #c2b280; -fx-text-fill: white; -fx-background-radius: 12;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #a99e75; -fx-text-fill: white; -fx-background-radius: 12;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #c2b280; -fx-text-fill: white; -fx-background-radius: 12;"));
        btn.setEffect(new DropShadow(5, Color.rgb(0,0,0,0.1)));
        return btn;
    }

    private Button createBorderedButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(320, 50);
        btn.setFont(Font.font("Arial", 16));
        btn.setStyle(
            "-fx-background-color: transparent; " +
                "-fx-border-color: #c2b280; " +
                "-fx-text-fill: #3e2723; " +
                "-fx-background-radius: 12; -fx-border-radius: 12;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #f5e4c4; " +
                "-fx-border-color: #a99e75; -fx-text-fill: #3e2723; " +
                "-fx-background-radius: 12; -fx-border-radius: 12;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent; " +
                "-fx-border-color: #c2b280; -fx-text-fill: #3e2723; " +
                "-fx-background-radius: 12; -fx-border-radius: 12;"
        ));
        btn.setEffect(new DropShadow(5, Color.rgb(0,0,0,0.1)));
        return btn;
    }

    private void playSandAnimation(Pane pane) {
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            double w = pane.getWidth(), h = pane.getHeight();
            Circle c = new Circle(2, Color.web("#000000", 0.4));
            c.setCenterX(Math.random() * w);
            c.setCenterY(h);
            pane.getChildren().add(c);
            TranslateTransition tt = new TranslateTransition(Duration.seconds(3), c);
            tt.setByY(-h);
            tt.setByX(Math.random() * 60 - 30);
            tt.setOnFinished(ev -> pane.getChildren().remove(c));
            tt.play();
        }));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.setRate(1.2);
        tl.play();
    }
}