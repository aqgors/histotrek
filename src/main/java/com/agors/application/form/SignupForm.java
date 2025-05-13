package com.agors.application.form;

import com.agors.application.window.MessageBox;
import com.agors.domain.entity.User;
import com.agors.domain.validation.SignupValidator;
import com.agors.infrastructure.persistence.impl.UserDaoImpl;
import com.agors.infrastructure.util.PasswordUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Форма реєстрації нового користувача.
 * <p>
 * Відображає поля для введення логіну, email та пароля,
 * перевіряє їх через SignupValidator, створює запис у БД
 * та показує підтвердження через MessageBox.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class SignupForm {

    /**
     * Показує форму реєстрації у новому вікні.
     *
     * @param stage об’єкт Stage для цієї форми
     * @param owner батьківське вікно, до якого повернутись
     */
    public void show(Stage stage, Stage owner) {
        stage.initOwner(owner);
        stage.setFullScreen(owner.isFullScreen());
        stage.setFullScreenExitHint("");
        stage.setTitle("Sign up");

        TextField userField    = styledField("Username");
        TextField emailField   = styledField("Email");
        PasswordField passField = (PasswordField) styledField("Password");

        Label userErr  = errorLabel();
        Label emailErr = errorLabel();
        Label passErr  = errorLabel();

        Button submit = styledButton(
            "Create an account",
            "-fx-background-color:#c2b280;",
            e -> handleSubmit(userField, emailField, passField, userErr, emailErr, passErr)
        );

        Button back = styledButton(
            "Back",
            "-fx-background-color:transparent;",
            e -> handleBack(stage, owner)
        );

        VBox fields = new VBox(10,
            userField,  userErr,
            emailField, emailErr,
            passField,  passErr,
            submit, back
        );
        fields.setAlignment(Pos.CENTER);

        Text title = new Text("Sign up");
        title.setFont(Font.font("Arial", 32));
        title.setFill(Color.web("#3e2723"));
        title.setEffect(new DropShadow(3, Color.rgb(0, 0, 0, 0.15)));

        VBox formBox = new VBox(20, title, fields);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(40));

        Pane sand = new Pane();
        sand.setMouseTransparent(true);
        animateSand(sand);

        StackPane root = new StackPane(sand, formBox);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #fdf6e3, #e29264);");

        Scene scene = new Scene(root, 800, 600);
        bindSize(sand, scene);

        scene.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
        stage.setFullScreenExitHint("");

        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
        formBox.requestFocus();
    }

    /**
     * Обробляє натискання кнопки "Create an account":
     * валідовує поля, створює користувача в БД та показує повідомлення.
     */
    private void handleSubmit(
        TextField userField,
        TextField emailField,
        PasswordField passField,
        Label userErr,
        Label emailErr,
        Label passErr
    ) {
        var errs = SignupValidator.validate(
            userField.getText(),
            emailField.getText(),
            passField.getText()
        );
        userErr.setText(errs.getOrDefault("username", ""));
        emailErr.setText(errs.getOrDefault("email", ""));
        passErr.setText(errs.getOrDefault("password", ""));
        if (errs.isEmpty()) {
            User u = new User();
            u.setUsername(userField.getText());
            u.setEmail(emailField.getText());
            u.setPasswordHash(PasswordUtil.hashPassword(passField.getText()));
            u.setRole("USER");
            new UserDaoImpl().addUser(u);
            MessageBox.show("Success", "Registration was successful!");
            userField.clear();
            emailField.clear();
            passField.clear();
        }
    }

    /**
     * Обробляє натискання кнопки "Back": повертає попереднє вікно.
     */
    private void handleBack(Stage stage, Stage owner) {
        owner.setFullScreen(stage.isFullScreen());
        stage.close();
        owner.show();
    }

    /**
     * Створює стилізоване поле введення з підказкою.
     *
     * @param prompt текст-підказка всередині поля
     * @return налаштований TextField або PasswordField
     */
    private TextField styledField(String prompt) {
        TextField f = prompt.equals("Password") ? new PasswordField() : new TextField();
        f.setPromptText(prompt);
        f.setMaxWidth(320);
        f.setPrefHeight(45);
        f.setFont(Font.font("Arial", 14));
        f.setStyle(
            "-fx-background-color:rgba(255,255,255,0.6);"
                + "-fx-border-color:#d3d3d3;"
                + "-fx-background-radius:8;"
                + "-fx-border-radius:8;"
                + "-fx-padding:0 10;"
        );
        return f;
    }

    /**
     * Створює порожню мітку для виводу повідомлень про помилки.
     */
    private Label errorLabel() {
        Label l = new Label();
        l.setFont(Font.font("Arial", 12));
        l.setTextFill(Color.RED);
        l.setMaxWidth(320);
        l.setWrapText(true);
        return l;
    }

    /**
     * Створює стилізовану кнопку з hover-ефектом.
     *
     * @param text       текст на кнопці
     * @param baseStyle  базовий CSS-стиль
     * @param handler    обробник натискання
     * @return налаштований Button
     */
    private Button styledButton(
        String text,
        String baseStyle,
        javafx.event.EventHandler<javafx.event.ActionEvent> handler
    ) {
        Button b = new Button(text);
        b.setFont(Font.font("Arial", 16));
        b.setPrefSize(320, 50);
        String hover = baseStyle.contains("transparent")
            ? "-fx-background-color:#f5e4c4;"
            : "-fx-background-color:#a99e75;";
        b.setStyle(baseStyle + " -fx-text-fill:black; -fx-background-radius:12;");
        b.setOnMouseEntered(e -> b.setStyle(hover + " -fx-text-fill:white; -fx-background-radius:12;"));
        b.setOnMouseExited(e  -> b.setStyle(baseStyle + " -fx-text-fill:black; -fx-background-radius:12;"));
        b.setEffect(new DropShadow(5, Color.rgb(0,0,0,0.1)));
        b.setOnAction(handler);
        return b;
    }

    /**
     * Запускає анімацію падаючого піску у фоновому шарі.
     *
     * @param p Pane, на якому показуються частинки піску
     */
    private void animateSand(Pane p) {
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            double w = p.getWidth(), h = p.getHeight();
            Circle c = new Circle(2, Color.web("#000000", 0.4));
            c.setCenterX(Math.random() * w);
            c.setCenterY(h);
            p.getChildren().add(c);
            TranslateTransition tt = new TranslateTransition(Duration.seconds(3), c);
            tt.setByY(-h);
            tt.setByX(Math.random() * 60 - 30);
            tt.setOnFinished(ev -> p.getChildren().remove(c));
            tt.play();
        }));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
    }

    /**
     * Прив'язує розміри Pane до розмірів Scene.
     *
     * @param p панель, яку слід зв’язати
     * @param s сцена, розміри якої використовуються
     */
    private void bindSize(Pane p, Scene s) {
        p.prefWidthProperty().bind(s.widthProperty());
        p.prefHeightProperty().bind(s.heightProperty());
    }
}
