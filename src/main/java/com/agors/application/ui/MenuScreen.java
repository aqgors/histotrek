package com.agors.application.ui;

import com.agors.application.auth.LoginWindow;
import com.agors.application.auth.SignupWindow;
import com.agors.infrastructure.util.I18n;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Стартове вікно застосунку Histotrek.
 * <p>
 * Дозволяє користувачу вибрати режим роботи: авторизація, реєстрація або гостьовий перегляд.
 * Також підтримується вибір мови інтерфейсу та анімований фон (пісок).
 * </p>
 * Перехід на відповідні вікна реалізовано через {@link LoginWindow}, {@link SignupWindow} та {@link GuestWindow}.
 *
 * @author agors
 * @version 1.0
 */
public class MenuScreen {

    /**
     * Відображає стартове вікно меню.
     *
     * @param stage головна сцена застосунку
     */
    public void show(Stage stage) {
        Text title = new Text("HISTOTREK");
        title.setFont(Font.font("Arial", 44));
        title.setFill(Color.web("#3e2723"));
        title.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.25)));

        VBox topSection = new VBox(title);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(30, 0, 0, 0));

        Button loginButton = createAnimatedButton(I18n.get("log_in"));
        loginButton.setOnAction(e -> {
            Stage loginStage = new Stage();
            stage.hide();
            new LoginWindow().show(loginStage, stage);
        });

        Button signupButton = createAnimatedButton(I18n.get("sign_up"));
        signupButton.setOnAction(e -> {
            Stage signupStage = new Stage();
            stage.hide();
            new SignupWindow().show(signupStage, stage);
        });

        Button guestButton = createAnimatedButton(I18n.get("log_in_guest"));
        guestButton.setOnAction(e -> new GuestWindow().start(stage));

        ChoiceBox<String> langChoice = new ChoiceBox<>();
        langChoice.getItems().addAll("Українська", "English");
        langChoice.setValue(I18n.getCurrentLocale().getLanguage().equals("en") ? "English" : "Українська");
        langChoice.setStyle("-fx-font-size: 13px; -fx-background-radius: 10; -fx-max-width: 160px;");
        langChoice.setOnAction(e -> {
            String lang = langChoice.getValue();
            if ("English".equals(lang)) I18n.setLanguage("en");
            else I18n.setLanguage("uk");
            stage.close();
            show(new Stage());
        });

        VBox centerSection = new VBox(20, loginButton, signupButton, guestButton, langChoice);
        centerSection.setAlignment(Pos.CENTER);
        centerSection.setPadding(new Insets(20));

        Text footer = new Text("© 2025 Histotrek Historical Journey");
        footer.setFont(Font.font("Arial", 12));
        footer.setFill(Color.GRAY);

        VBox bottomSection = new VBox(footer);
        bottomSection.setAlignment(Pos.BOTTOM_CENTER);
        bottomSection.setPadding(new Insets(0, 0, 15, 0));

        FadeTransition fadeButtons = new FadeTransition(Duration.seconds(1.2), centerSection);
        centerSection.setOpacity(0);
        fadeButtons.setToValue(1);
        fadeButtons.play();

        VBox layout = new VBox();
        layout.setSpacing(40);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(topSection, centerSection, bottomSection);

        Pane sandPane = new Pane();
        sandPane.setPickOnBounds(false);
        playSandAnimation(sandPane);

        StackPane content = new StackPane(layout);
        StackPane root = new StackPane(sandPane, content);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #fdf6e3, #e29264);");

        Scene scene = new Scene(root, 800, 600);
        sandPane.prefWidthProperty().bind(scene.widthProperty());
        sandPane.prefHeightProperty().bind(scene.heightProperty());

        scene.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
        stage.setFullScreenExitHint("");

        stage.setScene(scene);
        stage.setTitle("Histotrek");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    /**
     * Створює стилізовану кнопку з ефектом наведеного стану.
     *
     * @param text текст кнопки
     * @return об'єкт {@link Button} зі стилями і тінню
     */
    private Button createAnimatedButton(String text) {
        final String baseStyle = "-fx-background-color: #c2b280; -fx-text-fill: white; " +
            "-fx-font-size: 18px; -fx-background-radius: 15; -fx-cursor: hand;";
        final String hoverStyle = "-fx-background-color: #a99e75; -fx-text-fill: white; " +
            "-fx-font-size: 18px; -fx-background-radius: 15; -fx-cursor: hand;";

        Button button = new Button(text);
        button.setFont(Font.font("Arial", 18));
        button.setPrefWidth(260);
        button.setPrefHeight(55);
        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        button.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.15)));

        return button;
    }

    /**
     * Запускає нескінченну анімацію піщинок, які плавно рухаються вгору.
     * Ефект створюється на окремому шарі поверх фону.
     *
     * @param sandPane панель, на якій анімується пісок
     */
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
}
