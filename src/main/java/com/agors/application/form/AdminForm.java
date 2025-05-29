package com.agors.application.form;

import com.agors.application.window.ReportManagementTab;
import com.agors.application.window.UserManagementTab;
import com.agors.application.window.ContentManagementTab;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AdminForm {

    private final Stage previousStage;

    public AdminForm(Stage previousStage) {
        this.previousStage = previousStage;
    }

    public void show(Stage adminStage) {
        TabPane tabPane = new TabPane();

        Tab usersTab = new Tab("Користувачі", new UserManagementTab());
        usersTab.setClosable(false);

        Tab contentTab = new Tab("Контент", new ContentManagementTab());
        contentTab.setClosable(false);

        Tab reportsTab = new Tab("Звіти", new ReportManagementTab());
        reportsTab.setClosable(false);

        tabPane.getTabs().addAll(usersTab, contentTab, reportsTab);

        Button backBtn = new Button("↩ Назад");
        backBtn.setFont(Font.font("Arial", 14));
        backBtn.setOnAction(e -> {
            adminStage.close();
            previousStage.close();
            Stage newSettingsStage = new Stage();
            new SettingsForm(newSettingsStage).show(newSettingsStage);
        });

        HBox topBar = new HBox(backBtn);
        topBar.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 900, 600);
        adminStage.setTitle("Адмін-панель");
        adminStage.setScene(scene);
        adminStage.show();
    }
}
