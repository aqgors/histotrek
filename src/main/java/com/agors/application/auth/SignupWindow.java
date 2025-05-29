package com.agors.application.auth;

import com.agors.application.ui.MessageBox;
import com.agors.domain.entity.User;
import com.agors.domain.validation.SignupValidator;
import com.agors.infrastructure.persistence.impl.UserDaoImpl;
import com.agors.infrastructure.util.I18n;
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

public class SignupWindow {

    public void show(Stage stage, Stage owner) {
        stage.initOwner(owner);
        stage.setFullScreen(owner.isFullScreen());
        stage.setFullScreenExitHint("");
        stage.setTitle(I18n.get("sign_up_title"));

        TextField userField  = styledField(I18n.get("signup_username"));
        TextField emailField = styledField(I18n.get("signup_email"));
        PasswordField passField = styledPasswordField(I18n.get("signup_password"));

        Label userErr  = errorLabel();
        Label emailErr = errorLabel();
        Label passErr  = errorLabel();

        Button submit = styledButton(
            I18n.get("create_account"),
            "-fx-background-color:#c2b280;",
            e -> handleSubmit(userField, emailField, passField, userErr, emailErr, passErr)
        );

        Button back = styledButton(
            I18n.get("sign_up_back"),
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

        Text title = new Text(I18n.get("sign_up_title"));
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
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
        formBox.requestFocus();
    }

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
        userErr.setText(I18n.getOrDefault(errs.get("username")));
        emailErr.setText(I18n.getOrDefault(errs.get("email")));
        passErr.setText(I18n.getOrDefault(errs.get("password")));
        if (errs.isEmpty()) {
            User u = new User();
            u.setUsername(userField.getText());
            u.setEmail(emailField.getText());
            u.setPasswordHash(PasswordUtil.hashPassword(passField.getText()));
            u.setRole("USER");
            new UserDaoImpl().addUser(u);
            MessageBox.show(I18n.get("sign_up_success"), I18n.get("registration_success"));
            userField.clear();
            emailField.clear();
            passField.clear();
        }
    }

    private void handleBack(Stage stage, Stage owner) {
        owner.setFullScreen(stage.isFullScreen());
        stage.close();
        owner.show();
    }

    private TextField styledField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        applyFieldStyle(f);
        return f;
    }

    private PasswordField styledPasswordField(String prompt) {
        PasswordField f = new PasswordField();
        f.setPromptText(prompt);
        applyFieldStyle(f);
        return f;
    }

    private void applyFieldStyle(TextField f) {
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
    }

    private Label errorLabel() {
        Label l = new Label();
        l.setFont(Font.font("Arial", 12));
        l.setTextFill(Color.RED);
        l.setMaxWidth(320);
        l.setWrapText(true);
        return l;
    }

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

    private void bindSize(Pane p, Scene s) {
        p.prefWidthProperty().bind(s.widthProperty());
        p.prefHeightProperty().bind(s.heightProperty());
    }
}
