package com.agors.application.ui;

import com.agors.application.admin.AdminWindow;
import com.agors.domain.entity.User;
import com.agors.domain.validation.SettingsValidator;
import com.agors.infrastructure.persistence.impl.UserDaoImpl;
import com.agors.infrastructure.util.I18n;
import com.agors.infrastructure.util.PasswordUtil;
import com.agors.infrastructure.util.SessionContext;
import com.agors.infrastructure.util.ThemeManager;
import com.agors.domain.enums.ThemeType;
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

/**
 * Вікно налаштувань користувача в застосунку Histotrek.
 * <p>
 * Дозволяє змінювати ім’я користувача, email, пароль, тему, мову, а також видаляти акаунт,
 * переглядати інформацію про розробника, отримувати права адміністратора.
 * Має структуру з секціями, кнопками та діалогами підтвердження.
 * </p>
 * Підтримує повноекранний режим, застосування теми в реальному часі та локалізацію.
 *
 * @author agors
 * @version 1.0
 */
public class SettingsWindow {

    /** Посилання на вікно, з якого було відкрито налаштування. */
    private final Stage parentStage;
    /** Валідатор для перевірки нових значень, що вводяться. */
    private final SettingsValidator validator = new SettingsValidator();
    /** DAO для оновлення користувача в базі. */
    private final UserDaoImpl userDao = new UserDaoImpl();
    /** Поточний авторизований користувач. */
    private final User currentUser = SessionContext.getCurrentUser();
    /** Поточне вікно налаштувань. */
    private Stage currentSettingsStage;

    /**
     * Конструктор ініціалізує налаштування з батьківським вікном.
     *
     * @param parentStage вікно, яке відкрило форму налаштувань
     */
    public SettingsWindow(Stage parentStage) {
        this.parentStage = parentStage;
    }

    /**
     * Відображає вікно налаштувань користувача з усіма секціями.
     *
     * @param settingsStage сцена, у якій буде відкрито вікно
     */
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
            new UserWindow().start(userFormStage, userId, isFullScreen);
        });

        Text title = new Text(I18n.get("settings", "Settings"));
        title.setFont(Font.font("Arial", 14));
        title.setFill(Color.BLACK);

        HBox topBar = new HBox(10, backBtn, title);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15));

        VBox content = new VBox(20,
            createSection(I18n.get("profile", "Profile"),
                createStyledButton(I18n.get("change_username", "👤 Change username"), e -> {
                    if (confirmPassword(settingsStage)) handleChangeUsername(settingsStage);
                }),
                createStyledButton(I18n.get("update_email", "📧 Update email"), e -> {
                    if (confirmPassword(settingsStage)) handleChangeEmail(settingsStage);
                }),
                createStyledButton(I18n.get("change_password", "🔑 Change password"), e -> {
                    if (confirmPassword(settingsStage)) handleChangePassword(settingsStage);
                }),
                createStyledButton(I18n.get("delete_account", "🗑 Delete account"), e -> {
                    if (confirmPassword(settingsStage)) handleDeleteAccount(settingsStage);
                })
            ),
            createSection(I18n.get("personalization", "Personalization"),
                createThemeChanger(),
                createLanguageChoice()
            ),
            createSection(I18n.get("administration", "Administration"),
                createStyledButton(I18n.get("admin_access", "👑 Get admin access"), e -> handleAdminAccess(settingsStage))
            ),
            createSection(I18n.get("support", "Support & Info"),
                createStaticText(I18n.get("description", "Histotrek — a guide to historical places with ratings and reviews.")),
                createStaticText(I18n.get("version", "Version: 1.0")),
                createStaticText(I18n.get("developer", "Developer: agors")),
                createEmailLink("c.kovalchuk.oleksandr@student.uzhnu.edu.ua"),
                createStaticText(I18n.get("thank_you", "Thank you for using our application!"))
            )
        );
        content.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("settings-scrollpane");

        BorderPane root = new BorderPane(scrollPane);
        root.setTop(topBar);

        Scene scene = new Scene(root, 800, 600);

        scene.getStylesheets().add(getClass().getResource("/style/settings.css").toExternalForm());

        ThemeManager.applyTheme(scene);

        scene.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.F11) {
                settingsStage.setFullScreen(!settingsStage.isFullScreen());
            }
        });

        ThemeManager.addThemeChangeListener(theme -> ThemeManager.applyTheme(scene));

        settingsStage.setScene(scene);
        settingsStage.setTitle(I18n.get("settings", "Settings"));
        settingsStage.setMinWidth(800);
        settingsStage.setMinHeight(600);
        settingsStage.show();
    }

    /**
     * Обробка доступу до адмін-панелі. Після підтвердження пароля
     * та/або введення коду адміну відкриває {@link AdminWindow}.
     *
     * @param owner поточне вікно
     */
    private void handleAdminAccess(Stage owner) {
        if (!confirmPassword(owner)) return;

        User freshUser = userDao.getUserById(currentUser.getId());
        if (freshUser == null) return;

        SessionContext.setCurrentUser(freshUser);
        if (freshUser.getRole().equalsIgnoreCase("ADMIN")) {
            owner.close();
            Stage adminStage = new Stage();
            new AdminWindow(owner).show(adminStage);
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(I18n.get("admin_access_title", "Administrative Access"));
        dialog.setHeaderText(I18n.get("admin_access_prompt", "Enter admin password"));
        dialog.initOwner(owner);

        var result = dialog.showAndWait();
        if (result.isEmpty()) return;

        if ("0000".equals(result.get())) {
            freshUser.setRole("ADMIN");
            userDao.updateUser(freshUser);
            SessionContext.setCurrentUser(freshUser);

            MessageBox.show(
                I18n.get("access_granted_title", "Access Granted"),
                I18n.get("access_granted_msg", "You have been granted admin rights"),
                owner
            );
            owner.close();
            Stage adminStage = new Stage();
            new AdminWindow(owner).show(adminStage);
        } else {
            MessageBox.show(
                I18n.get("error_title_settings", "Error"),
                I18n.get("admin_access_failed", "Incorrect admin password"),
                owner
            );
        }

    }

    /**
     * Створює статичний текст для відображення в секції «Support & Info».
     *
     * @param text текст повідомлення
     * @return {@link Label} з відповідним стилем
     */
    private Label createStaticText(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", 13));
        label.setWrapText(true);
        label.setTextFill(Color.web("#333"));
        return label;
    }

    /**
     * Створює клікабельний email-лінк для звернення до розробника.
     *
     * @param email email-адреса
     * @return {@link Hyperlink}, що відкриває Gmail
     */
    private Hyperlink createEmailLink(String email) {
        Hyperlink link = new Hyperlink(I18n.get("contact_us", "✉ Contact us: ") + email);
        link.setFont(Font.font("Arial", 13));
        link.setTextFill(Color.DARKBLUE);
        link.setBorder(Border.EMPTY);
        link.setOnAction(e -> {
            String uri = "https://mail.google.com/mail/?view=cm&fs=1&to=" + email;
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(uri));
            } catch (Exception ex) {
                MessageBox.show(
                    I18n.get("error_title_settings", "Error"),
                    I18n.get("email_client_error", "Failed to open mail client."),
                    currentSettingsStage
                );
            }
        });
        return link;
    }

    /**
     * Створює кнопку зміни теми з діалогом вибору.
     *
     * @return {@link Button} для зміни теми
     */
    private Button createThemeChanger() {
        Button btn = new Button(I18n.get("theme_button", "🎨 Apply another theme"));
        btn.setFont(Font.font("Arial", 14));
        btn.setStyle("-fx-background-color: #c2b280; -fx-text-fill: black; -fx-background-radius: 8;");
        btn.setCursor(Cursor.HAND);
        btn.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.2)));

        btn.setOnAction(e -> {
            var map = new java.util.LinkedHashMap<String, ThemeType>();
            map.put(I18n.get("theme_default", "Default"), ThemeType.DEFAULT);
            map.put(I18n.get("theme_light", "Light"), ThemeType.LIGHT);
            map.put(I18n.get("theme_dark", "Dark"), ThemeType.DARK);

            String currentLocalized = map.entrySet().stream()
                .filter(entry -> entry.getValue() == ThemeManager.getCurrentTheme())
                .map(java.util.Map.Entry::getKey)
                .findFirst()
                .orElse(I18n.get("theme_default", "Default"));

            ChoiceDialog<String> dialog = new ChoiceDialog<>(currentLocalized, map.keySet().toArray(new String[0]));
            dialog.setTitle(I18n.get("theme_title", "Theme"));
            dialog.setHeaderText(I18n.get("theme_header", "Select a theme"));
            dialog.setContentText(I18n.get("theme_content", "Theme:"));

            dialog.showAndWait().ifPresent(choice -> {
                ThemeType selected = map.get(choice);
                if (selected != null) {
                    ThemeManager.setTheme(selected);
                }
            });
        });

        return btn;
    }

    /**
     * Створює вибір мови (українська / англійська).
     *
     * @return {@link ChoiceBox} для перемикання мови
     */
    private ChoiceBox<String> createLanguageChoice() {
        ChoiceBox<String> cb = new ChoiceBox<>();
        cb.getItems().addAll(
            I18n.get("lang_ukrainian", "Українська"),
            I18n.get("lang_english", "English")
        );
        cb.setValue(I18n.getCurrentLocale().getLanguage().equals("en")
            ? I18n.get("lang_english", "English")
            : I18n.get("lang_ukrainian", "Українська"));
        cb.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        cb.setOnAction(e -> {
            String lang = cb.getValue();
            if (lang.equals(I18n.get("lang_english", "English"))) I18n.setLanguage("en");
            else I18n.setLanguage("uk");
            currentSettingsStage.close();
            show(new Stage());
        });
        return cb;
    }

    /**
     * Створює секцію налаштувань з заголовком і переданими елементами керування.
     *
     * @param heading назва секції
     * @param controls елементи керування (кнопки, текст)
     * @return контейнер {@link VBox} із секцією
     */
    private VBox createSection(String heading, javafx.scene.Node... controls) {
        Text h = new Text(heading);
        h.setFont(Font.font("Arial", 14));
        h.setFill(Color.BLACK);
        VBox box = new VBox(10, h);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8;");
        box.getChildren().addAll(controls);
        return box;
    }

    /**
     * Створює стилізовану кнопку з ефектами наведення.
     *
     * @param text текст кнопки
     * @param handler обробник події
     * @return об’єкт {@link Button}
     */
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

    /**
     * Створює кнопку «Назад» у вигляді круга зі стрілкою.
     *
     * @return стилізована кнопка {@link Button}
     */
    private Button createBackButton() {
        StackPane circle = new StackPane();
        circle.setPrefSize(36, 36);
        circle.getStyleClass().add("back-button-circle");

        Text arrow = new Text("\u2190");
        arrow.setFont(Font.font("Arial", 14));
        arrow.getStyleClass().add("back-button-arrow");

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

    /**
     * Обробляє зміну імені користувача.
     *
     * @param owner вікно, у якому відбувається зміна
     */
    private void handleChangeUsername(Stage owner) {
        String newName = askInput(I18n.get("prompt_new_username", "New username:"), owner);
        if (newName == null) return;

        String err = validator.validateUsername(newName, currentUser.getUsername());
        if (err != null) {
            MessageBox.show(
                I18n.get("error_title_settings", "Error"),
                err,
                owner
            );
            return;
        }

        currentUser.setUsername(newName);
        userDao.updateUser(currentUser);
        MessageBox.show(
            I18n.get("success_settings", "Success"),
            I18n.get("username_updated", "Username updated successfully"),
            owner
        );
    }

    /**
     * Обробляє зміну email-адреси користувача.
     *
     * @param owner вікно, у якому відбувається зміна
     */
    private void handleChangeEmail(Stage owner) {
        String newEmail = askInput(I18n.get("prompt_new_email", "New email:"), owner);
        if (newEmail == null) return;

        String err = validator.validateEmail(newEmail, currentUser.getEmail());
        if (err != null) {
            MessageBox.show(
                I18n.get("error_title_settings", "Error"),
                err,
                owner
            );
            return;
        }

        currentUser.setEmail(newEmail);
        userDao.updateUser(currentUser);
        MessageBox.show(
            I18n.get("success_settings", "Success"),
            I18n.get("email_updated", "Email updated successfully"),
            owner
        );
    }

    /**
     * Обробляє зміну пароля користувача.
     *
     * @param owner вікно, у якому відбувається зміна
     */
    private void handleChangePassword(Stage owner) {
        String newPass = askInput(I18n.get("prompt_new_password", "New password:"), owner);
        if (newPass == null) return;

        String err = validator.validatePassword(newPass);
        if (err != null) {
            MessageBox.show(
                I18n.get("error_title_settings", "Error"),
                err,
                owner
            );
            return;
        }

        currentUser.setPasswordHash(PasswordUtil.hashPassword(newPass));
        userDao.updateUser(currentUser);
        MessageBox.show(
            I18n.get("success_settings", "Success"),
            I18n.get("password_updated", "Password updated successfully"),
            owner
        );
    }

    /**
     * Обробляє видалення акаунта користувача з підтвердженням.
     *
     * @param owner вікно, у якому відбувається дія
     */
    private void handleDeleteAccount(Stage owner) {
        boolean confirmed = MessageBox.showConfirm(
            I18n.get("confirm_title_settings", "Confirmation"),
            I18n.get("confirm_delete_account", "Are you sure you want to delete your account?"),
            owner
        );
        if (!confirmed) return;

        userDao.deleteUser(currentUser.getId());
        MessageBox.show(
            I18n.get("success_settings", "Success"),
            I18n.get("account_deleted", "Your account has been deleted"),
            owner
        );
        owner.close();
        new MenuScreen().show(new Stage());
    }

    /**
     * Відкриває діалог підтвердження пароля користувача.
     *
     * @param owner вікно, в якому викликається підтвердження
     * @return true, якщо пароль вірний
     */
    private boolean confirmPassword(Stage owner) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(I18n.get("confirm_password_title", "Password Confirmation"));
        dialog.setHeaderText(I18n.get("confirm_password_header", "Enter current password"));
        dialog.initOwner(owner);

        var res = dialog.showAndWait();
        if (res.isEmpty()) return false;

        String inputHash = PasswordUtil.hashPassword(res.get());
        if (!inputHash.equals(currentUser.getPasswordHash())) {
            MessageBox.show(
                I18n.get("error_title_settings", "Error"),
                I18n.get("error_wrong_password", "Incorrect password"),
                owner
            );
            return false;
        }
        return true;
    }

    /**
     * Відображає діалог із полем введення для зміни значення (username/email/password).
     *
     * @param msg повідомлення або підказка
     * @param owner вікно, до якого прив’язаний діалог
     * @return введене користувачем значення або null
     */
    private String askInput(String msg, Stage owner) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(I18n.get("input_dialog_title", "Change value"));
        dialog.setHeaderText(msg);
        dialog.initOwner(owner);
        return dialog.showAndWait().orElse(null);
    }
}
