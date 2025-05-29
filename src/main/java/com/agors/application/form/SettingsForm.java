package com.agors.application.form;

import com.agors.application.window.MenuScreen;
import com.agors.application.window.MessageBox;
import com.agors.domain.entity.User;
import com.agors.domain.validation.SettingsValidator;
import com.agors.infrastructure.persistence.impl.UserDaoImpl;
import com.agors.infrastructure.util.PasswordUtil;
import com.agors.infrastructure.util.SessionContext;
import com.agors.infrastructure.util.ThemeManager;
import com.agors.infrastructure.util.enums.ThemeType;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SettingsForm {

    private final Stage parentStage;
    private final SettingsValidator validator = new SettingsValidator();
    private final UserDaoImpl userDao = new UserDaoImpl();
    private final User currentUser = SessionContext.getCurrentUser();
    private Stage currentSettingsStage;

    public SettingsForm(Stage parentStage) {
        this.parentStage = parentStage;
    }

    public void show(Stage settingsStage) {
        this.currentSettingsStage = settingsStage;
        parentStage.hide();
        settingsStage.setFullScreen(parentStage.isFullScreen());
        settingsStage.setFullScreenExitHint("");

        Button backBtn = createBackButton();
        backBtn.setOnAction(e -> {
            settingsStage.close();
            Stage userFormStage = new Stage();
            int userId = currentUser.getId();
            boolean isFullScreen = parentStage.isFullScreen();
            new UserForm().start(userFormStage, userId, isFullScreen);
        });

        Text title = new Text("Settings");
        title.setFont(Font.font("Arial", 14));
        title.setFill(Color.BLACK);

        HBox topBar = new HBox(10, backBtn, title);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15));

        VBox content = new VBox(20,
            createSection("Profile",
                createStyledButton("👤 Змінити ім'я користувача", e -> {
                    if (confirmPassword(settingsStage)) handleChangeUsername(settingsStage);
                }),
                createStyledButton("📧 Оновити електронну пошту", e -> {
                    if (confirmPassword(settingsStage)) handleChangeEmail(settingsStage);
                }),
                createStyledButton("🔑 Змінити пароль", e -> {
                    if (confirmPassword(settingsStage)) handleChangePassword(settingsStage);
                }),
                createStyledButton("🗑 Видалити акаунт", e -> {
                    if (confirmPassword(settingsStage)) handleDeleteAccount(settingsStage);
                })
            ),
            createSection("Personalization",
                createThemeChanger(),
                createLanguageChoice()
            ),
            createSection("Administration",
                createStyledButton("👑 Отримати права адміністратора", e -> handleAdminAccess(settingsStage))
            ),
            createSection("Support & Info",
                createStaticText("Histotrek — довідник історичних місць з можливістю додавання відгуків та рейтингів."),
                createStaticText("Версія: 1.0"),
                createStaticText("Розробник: agors"),
                createEmailLink("c.kovalchuk.oleksandr@student.uzhnu.edu.ua"),
                createStaticText("Дякуємо за використання нашого застосунку!")
            )
        );
        content.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        BorderPane root = new BorderPane(scrollPane);
        root.setTop(topBar);

        Scene scene = new Scene(root, 800, 600);

        scene.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.F11) {
                settingsStage.setFullScreen(!settingsStage.isFullScreen());
            }
        });

        settingsStage.setScene(scene);
        settingsStage.setTitle("Settings");
        settingsStage.setMinWidth(800);
        settingsStage.setMinHeight(600);
        settingsStage.show();
    }

    private Label createStaticText(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", 13));
        label.setWrapText(true);
        label.setTextFill(Color.web("#333"));
        return label;
    }

    private Hyperlink createEmailLink(String email) {
        Hyperlink link = new Hyperlink("✉ Написати нам: " + email);
        link.setFont(Font.font("Arial", 13));
        link.setTextFill(Color.DARKBLUE);
        link.setBorder(Border.EMPTY);
        link.setOnAction(e -> {
            String uri = "https://mail.google.com/mail/?view=cm&fs=1&to=" + email;
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(uri));
            } catch (Exception ex) {
                MessageBox.show("Помилка", "Не вдалося відкрити поштовий клієнт.");
            }
        });
        return link;
    }

    private void handleAdminAccess(Stage owner) {
        if (!confirmPassword(owner)) return;

        // Перечитати користувача з БД для оновлення ролі
        User freshUser = userDao.getUserById(currentUser.getId());
        if (freshUser == null) return;

        SessionContext.setCurrentUser(freshUser); // оновити сесію
        if (freshUser.getRole().equalsIgnoreCase("ADMIN")) {
            owner.close();
            Stage adminStage = new Stage();
            new AdminForm(owner).show(adminStage);
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Адміністративний доступ");
        dialog.setHeaderText("Введіть пароль адміністратора");
        dialog.initOwner(owner);

        var result = dialog.showAndWait();
        if (result.isEmpty()) return;

        if ("0000".equals(result.get())) {
            freshUser.setRole("ADMIN");
            userDao.updateUser(freshUser);
            SessionContext.setCurrentUser(freshUser); // оновити сесію ще раз

            MessageBox.show("Доступ надано", "Ви отримали права адміністратора");
            owner.close();
            Stage adminStage = new Stage();
            new AdminForm(owner).show(adminStage);
        } else {
            MessageBox.show("Помилка", "Невірний пароль адміністратора");
        }
    }

    private Button createThemeChanger() {
        Button btn = new Button("🎨 Застосувати іншу тему до меню");
        btn.setFont(Font.font("Arial", 14));
        btn.setStyle("-fx-background-color: #c2b280; -fx-text-fill: black; -fx-background-radius: 8;");
        btn.setCursor(Cursor.HAND);
        btn.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.2)));

        btn.setOnAction(e -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Default", "Default", "Light", "Dark");
            dialog.setTitle("Тема");
            dialog.setHeaderText("Оберіть тему для меню користувача");
            dialog.setContentText("Тема:");

            dialog.showAndWait().ifPresent(choice -> {
                ThemeType selected = ThemeType.valueOf(choice.toUpperCase());
                ThemeManager.setTheme(selected);
            });
        });

        return btn;
    }

    private VBox createSection(String heading, javafx.scene.Node... controls) {
        Text h = new Text(heading);
        h.setFont(Font.font("Arial", 14));
        h.setFill(Color.BLACK);
        VBox box = new VBox(10, h);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: white; -fx-border-color: #d3d3d3; -fx-border-radius: 8; -fx-background-radius: 8;");
        box.getChildren().addAll(controls);
        return box;
    }

    private Button createStyledButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", 14));
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #c2b280; -fx-text-fill: black; -fx-background-radius: 8;");
        btn.setCursor(Cursor.HAND);
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #a99e75; -fx-text-fill: black; -fx-background-radius: 8;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #c2b280; -fx-text-fill: black; -fx-background-radius: 8;"));
        btn.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.2)));
        if (handler != null) btn.setOnAction(handler);
        return btn;
    }

    private Button createStyledButton(String text) {
        return createStyledButton(text, null);
    }

    private ChoiceBox<String> createLanguageChoice() {
        ChoiceBox<String> cb = new ChoiceBox<>();
        cb.getItems().addAll("Українська", "English");
        cb.setValue("Українська");
        cb.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        return cb;
    }

    private Button createBackButton() {
        StackPane circle = new StackPane();
        circle.setPrefSize(36, 36);
        circle.setStyle("-fx-background-color: white; -fx-background-radius: 18;");
        circle.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.2)));

        Text arrow = new Text("\u2190");
        arrow.setFont(Font.font("Arial", 14));
        arrow.setFill(Color.web("#3e2723"));
        circle.getChildren().add(arrow);

        Button btn = new Button();
        btn.setGraphic(circle);
        btn.setBackground(Background.EMPTY);
        btn.setCursor(Cursor.HAND);

        ScaleTransition enter = new ScaleTransition(Duration.millis(150), circle);
        enter.setToX(1.1);
        enter.setToY(1.1);
        ScaleTransition exit = new ScaleTransition(Duration.millis(150), circle);
        exit.setToX(1.0);
        exit.setToY(1.0);
        btn.setOnMouseEntered(e -> enter.playFromStart());
        btn.setOnMouseExited(e -> exit.playFromStart());

        return btn;
    }

    private void handleChangeUsername(Stage owner) {
        String newName = askInput("Нове ім'я користувача:", owner);
        if (newName == null) return;

        String err = validator.validateUsername(newName, currentUser.getUsername());
        if (err != null) {
            MessageBox.show("Помилка", err);
            return;
        }

        currentUser.setUsername(newName);
        userDao.updateUser(currentUser);
        MessageBox.show("Успіх", "Ім'я оновлено успішно");
    }

    private void handleChangeEmail(Stage owner) {
        String newEmail = askInput("Нова електронна пошта:", owner);
        if (newEmail == null) return;

        String err = validator.validateEmail(newEmail, currentUser.getEmail());
        if (err != null) {
            MessageBox.show("Помилка", err);
            return;
        }

        currentUser.setEmail(newEmail);
        userDao.updateUser(currentUser);
        MessageBox.show("Успіх", "Email оновлено успішно");
    }

    private void handleChangePassword(Stage owner) {
        String newPass = askInput("Новий пароль:", owner);
        if (newPass == null) return;

        String err = validator.validatePassword(newPass);
        if (err != null) {
            MessageBox.show("Помилка", err);
            return;
        }

        currentUser.setPasswordHash(PasswordUtil.hashPassword(newPass));
        userDao.updateUser(currentUser);
        MessageBox.show("Успіх", "Пароль оновлено успішно");
    }

    private void handleDeleteAccount(Stage owner) {
        boolean confirmed = MessageBox.showConfirm("Підтвердження", "Ви впевнені, що хочете видалити акаунт?");
        if (!confirmed) return;

        userDao.deleteUser(currentUser.getId());
        MessageBox.show("Обліковий запис видалено", "Ваш акаунт успішно видалено");
        owner.close();
        new MenuScreen().show(new Stage());
    }

    private boolean confirmPassword(Stage owner) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Підтвердження пароля");
        dialog.setHeaderText("Введіть поточний пароль");
        dialog.initOwner(owner);

        var res = dialog.showAndWait();
        if (res.isEmpty()) return false;

        String inputHash = PasswordUtil.hashPassword(res.get());
        if (!inputHash.equals(currentUser.getPasswordHash())) {
            MessageBox.show("Помилка", "Невірний пароль");
            return false;
        }
        return true;
    }

    private String askInput(String msg, Stage owner) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Зміна значення");
        dialog.setHeaderText(msg);
        dialog.initOwner(owner);
        return dialog.showAndWait().orElse(null);
    }
}