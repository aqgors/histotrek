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
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AdminWindow {

    private final Stage previousStage;

    public AdminWindow(Stage previousStage) {
        this.previousStage = previousStage;
    }

    public void show(Stage adminStage) {
        TabPane tabPane = new TabPane();

        Tab usersTab = new Tab(I18n.get("admin_tab_users", "Users"), new UserManagementTab());
        usersTab.setClosable(false);

        Tab contentTab = new Tab(I18n.get("admin_tab_content", "Content"), new ContentManagementTab());
        contentTab.setClosable(false);

        Tab reportsTab = new Tab(I18n.get("admin_tab_reports", "Reports"), new ReportManagementTab());
        reportsTab.setClosable(false);

        tabPane.getTabs().addAll(usersTab, contentTab, reportsTab);

        // Кнопка назад
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

        ThemeManager.applyTheme(scene);
        ThemeManager.addThemeChangeListener(theme -> ThemeManager.applyTheme(scene));

        scene.getStylesheets().add(getClass().getResource("/style/admin-tabs.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/style/content-tab.css").toExternalForm());

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
