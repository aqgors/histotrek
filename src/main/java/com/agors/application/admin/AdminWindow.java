package com.agors.application.admin;

import com.agors.application.ui.SettingsWindow;
import com.agors.infrastructure.util.I18n;
import com.agors.infrastructure.util.ThemeManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Вікно адміністратора, яке надає доступ до керування користувачами, контентом та звітами.
 * <p>
 * Це головне вікно для адміністративного інтерфейсу з трьома вкладками:
 * <ul>
 *   <li>Користувачі</li>
 *   <li>Контент</li>
 *   <li>Звіти</li>
 * </ul>
 * Має кнопку повернення до вікна налаштувань.
 * </p>
 */
public class AdminWindow {

    /** Попередній Stage, з якого було відкрито це вікно. */
    private final Stage previousStage;

    /**
     * Конструктор AdminWindow.
     *
     * @param previousStage вікно, яке було відкрито перед запуском адмін-панелі
     */
    public AdminWindow(Stage previousStage) {
        this.previousStage = previousStage;
    }

    /**
     * Відображає вікно адміністратора з вкладками керування.
     *
     * @param adminStage Stage для відображення адмін-інтерфейсу
     */
    public void show(Stage adminStage) {
        TabPane tabPane = new TabPane();

        // Вкладка користувачів
        Tab usersTab = new Tab(I18n.get("admin_tab_users", "Users"), new UserManagementTab());
        usersTab.setClosable(false);

        // Вкладка контенту
        Tab contentTab = new Tab(I18n.get("admin_tab_content", "Content"), new ContentManagementTab());
        contentTab.setClosable(false);

        // Вкладка звітів
        Tab reportsTab = new Tab(I18n.get("admin_tab_reports", "Reports"), new ReportManagementTab());
        reportsTab.setClosable(false);

        tabPane.getTabs().addAll(usersTab, contentTab, reportsTab);

        // Кнопка назад до налаштувань
        Button backBtn = new Button(I18n.get("back_button", "↩ Назад"));
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            adminStage.close();
            previousStage.close();
            Stage newSettingsStage = new Stage();
            new SettingsWindow(newSettingsStage).show(newSettingsStage);
        });

        HBox topBar = new HBox(backBtn);
        topBar.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 900, 600);

        // Застосування теми
        ThemeManager.applyTheme(scene);
        ThemeManager.addThemeChangeListener(theme -> ThemeManager.applyTheme(scene));

        // Стилі
        scene.getStylesheets().add(getClass().getResource("/style/admin-tabs.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/style/content-tab.css").toExternalForm());

        // F11 — повноекранний режим
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                adminStage.setFullScreen(!adminStage.isFullScreen());
            }
        });

        adminStage.setFullScreenExitHint("");
        adminStage.setTitle(I18n.get("admin_panel_title", "Admin Panel"));
        adminStage.setScene(scene);
        adminStage.show();
    }
}
