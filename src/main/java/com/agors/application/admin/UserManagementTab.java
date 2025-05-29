package com.agors.application.admin;

import com.agors.domain.entity.User;
import com.agors.infrastructure.persistence.impl.UserDaoImpl;
import com.agors.infrastructure.util.I18n;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.stream.Collectors;

public class UserManagementTab extends VBox {

    private final UserDaoImpl userDao = new UserDaoImpl();
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final VBox userListContainer = new VBox(10);

    public UserManagementTab() {
        setPadding(new Insets(20));
        setSpacing(15);
        setAlignment(Pos.TOP_CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText(I18n.get("user_search_prompt", "Пошук користувача..."));
        searchField.setMaxWidth(300);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateUserList(newVal));

        Label title = new Label(I18n.get("user_management_title", "Керування користувачами"));
        title.setFont(Font.font("Arial", 18));

        userListContainer.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(userListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent;");

        getChildren().addAll(title, searchField, scrollPane);
        updateUserList("");
    }

    private void updateUserList(String filter) {
        users.setAll(userDao.getAllUsers().stream()
            .filter(u -> u.getUsername().toLowerCase().contains(filter.toLowerCase()) ||
                u.getEmail().toLowerCase().contains(filter.toLowerCase()))
            .collect(Collectors.toList()));

        userListContainer.getChildren().clear();
        for (User user : users) {
            userListContainer.getChildren().add(createUserCard(user));
        }
    }

    private HBox createUserCard(User user) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 8;");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(5);
        Label name = new Label("👤 " + user.getUsername());
        Label email = new Label("📧 " + user.getEmail());
        name.setFont(Font.font("Arial", 14));
        email.setFont(Font.font("Arial", 14));
        name.setStyle("-fx-text-fill: black;");
        email.setStyle("-fx-text-fill: black;");
        infoBox.getChildren().addAll(name, email);

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("USER", "ADMIN");
        roleBox.setValue(user.getRole().toUpperCase());

        roleBox.setOnAction(e -> {
            String selected = roleBox.getValue();
            user.setRole(selected);
            userDao.updateUser(user);
        });

        Button deleteBtn = new Button("🗑 " + I18n.get("delete_user_btn", "Видалити"));
        deleteBtn.setStyle("-fx-background-color: #e57373; -fx-text-fill: white;");
        deleteBtn.setDisable(user.getId() == com.agors.infrastructure.util.SessionContext.getCurrentUser().getId());

        deleteBtn.setOnAction(e -> {
            boolean confirm = new Alert(Alert.AlertType.CONFIRMATION,
                I18n.get("confirm_delete_user", "Видалити користувача ") + user.getUsername() + "?",
                ButtonType.YES, ButtonType.NO)
                .showAndWait().filter(ButtonType.YES::equals).isPresent();
            if (confirm) {
                userDao.deleteUser(user.getId());
                updateUserList("");
            }
        });

        card.getChildren().addAll(infoBox, roleBox, deleteBtn);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        return card;
    }
}
