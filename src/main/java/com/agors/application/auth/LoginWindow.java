package com.agors.application.auth;

import com.agors.application.ui.MessageBox;
import com.agors.application.ui.UserWindow;
import com.agors.domain.entity.User;
import com.agors.domain.validation.LoginValidator;
import com.agors.infrastructure.util.I18n;
import com.agors.infrastructure.util.SessionContext;
import com.agors.infrastructure.persistence.impl.SessionDaoImpl;
import java.util.UUID;
import com.agors.infrastructure.persistence.impl.UserDaoImpl;
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
 * Вікно авторизації користувача у застосунку Histotrek.
 * <p>
 * Забезпечує введення логіна або email, пароля, валідацію введених даних,
 * повідомлення про помилки, а також ініціалізацію сесії користувача після успішного входу.
 * Має анімований фон із піском та сучасний вигляд кнопок і полів.
 * </p>
 * Підтримує повноекранний режим (перемикається клавішею F11).
 *
 * @author agors
 * @version 1.0
 */
public class LoginWindow {

    /**
     * Відображає вікно входу з анімацією, формою авторизації, кнопками та темою.
     *
     * @param stage поточне вікно авторизації
     * @param owner вікно, з якого викликається авторизація (наприклад, стартове вікно)
     */
    public void show(Stage stage, Stage owner) {
        stage.setFullScreen(owner.isFullScreen());
        stage.setFullScreenExitHint("");

        TextField loginField = styledTextField(I18n.get("username_or_email"));
        PasswordField pwField = styledPasswordField(I18n.get("password"));

        Label loginErr = styledError();
        Label pwErr    = styledError();

        Button loginBtn = styledButton(
            I18n.get("log_in"),
            "-fx-background-color:#c2b280;",
            e -> handleLogin(stage, owner, loginField, pwField, loginErr, pwErr)
        );
        Button backBtn = styledButton(
            I18n.get("back"),
            "-fx-background-color:transparent;",
            e -> {
                owner.setFullScreen(stage.isFullScreen());
                stage.close();
                owner.show();
            }
        );

        VBox form = new VBox(20,
            title(I18n.get("log_in_title")),
            loginField, loginErr,
            pwField,    pwErr,
            loginBtn,   backBtn
        );
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(40));

        Pane sand = new Pane();
        sand.setMouseTransparent(true);
        animateSand(sand);

        StackPane root = new StackPane(sand, form);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #fdf6e3, #e29264);");

        Scene scene = new Scene(root, 800, 600);
        bindSize(sand, scene);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        stage.setScene(scene);
        stage.setTitle("Histotrek – Login");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
        form.requestFocus();
    }

    /**
     * Обробляє авторизацію користувача:
     * виконує валідацію полів, перевіряє логін/email і пароль,
     * створює сесію у базі даних та відкриває {@link UserWindow}.
     *
     * @param stage поточне вікно
     * @param owner попереднє вікно (повертається до нього у разі невдачі)
     * @param lf    поле вводу логіна або email
     * @param pf    поле вводу пароля
     * @param e1    мітка для помилок логіна/email
     * @param e2    мітка для помилок пароля
     */
    private void handleLogin(Stage stage, Stage owner,
        TextField lf, PasswordField pf,
        Label e1, Label e2) {
        e1.setText("");
        e2.setText("");
        var errs = LoginValidator.validate(lf.getText(), pf.getText());
        e1.setText(I18n.getOrDefault(errs.get("login")));
        e2.setText(I18n.getOrDefault(errs.get("password")));
        if (errs.isEmpty()) {
            User u = new UserDaoImpl().getByUsernameOrEmail(lf.getText());
            SessionContext.setCurrentUser(u);
            String token = UUID.randomUUID().toString();
            new SessionDaoImpl().createSession(u.getId(), token);

            MessageBox.show(
                I18n.get("success_settings"),
                I18n.get("welcome") + ", " + u.getUsername() + "!",
                stage
            );

            stage.close();
            new UserWindow().start(owner, u.getId(), stage.isFullScreen());
        }
    }

    /**
     * Створює стилізований заголовок форми.
     *
     * @param t текст заголовку
     * @return об'єкт {@link Text} з відповідними стилями
     */
    private Text title(String t) {
        Text x = new Text(t);
        x.setFont(Font.font("Arial", 32));
        x.setFill(Color.web("#3e2723"));
        x.setEffect(new DropShadow(3, Color.rgb(0, 0, 0, 0.15)));
        return x;
    }

    /**
     * Створює стилізоване текстове поле з підказкою.
     *
     * @param prompt текст-підказка
     * @return об'єкт {@link TextField}
     */
    private TextField styledTextField(String prompt) {
        TextField f = new TextField();
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
     * Створює стилізоване поле для введення пароля.
     *
     * @param prompt текст-підказка
     * @return об'єкт {@link PasswordField}
     */
    private PasswordField styledPasswordField(String prompt) {
        PasswordField f = new PasswordField();
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
     * Створює стилізовану мітку для відображення повідомлень про помилки.
     *
     * @return об'єкт {@link Label} червоного кольору
     */
    private Label styledError() {
        Label l = new Label();
        l.setTextFill(Color.RED);
        l.setFont(Font.font(12));
        l.setMaxWidth(320);
        l.setWrapText(true);
        return l;
    }

    /**
     * Створює стилізовану кнопку з візуальними ефектами.
     *
     * @param text     текст кнопки
     * @param baseStyle CSS-стиль для звичайного стану
     * @param handler  обробник події натискання
     * @return об'єкт {@link Button}
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
        b.setStyle(baseStyle + " -fx-text-fill:#ffffff; -fx-background-radius:12;");
        b.setOnMouseEntered(e -> b.setStyle(hover + " -fx-text-fill:#0c0c0d; -fx-background-radius:12;"));
        b.setOnMouseExited(e  -> b.setStyle(baseStyle + " -fx-text-fill:#ffffff; -fx-background-radius:12;"));
        b.setEffect(new DropShadow(5, Color.rgb(0,0,0,0.1)));
        b.setOnAction(handler);
        return b;
    }

    /**
     * Анімує фон пісочними частинками, які піднімаються вгору.
     *
     * @param p шар, на якому відображаються частинки піску
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
        tl.setRate(1.2);
        tl.play();
    }

    /**
     * Прив'язує розміри шару до розмірів сцени.
     *
     * @param p панель (наприклад, фон)
     * @param s сцена, до якої прив'язується
     */
    private void bindSize(Pane p, Scene s) {
        p.prefWidthProperty().bind(s.widthProperty());
        p.prefHeightProperty().bind(s.heightProperty());
    }
}