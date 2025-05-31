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

import java.util.stream.Collectors;

/**
 * Вкладка адміністрування користувачів у панелі адміністратора.
 * <p>
 * Надає можливість переглядати список користувачів, фільтрувати їх за ім’ям або email,
 * змінювати ролі (USER / ADMIN) та видаляти користувачів, крім поточного.
 * </p>
 * Реалізовано з використанням JavaFX UI-компонентів.
 *
 * @author agors
 * @version 1.0
 */
public class UserManagementTab extends VBox {

    /** DAO-обʼєкт для доступу до користувачів у базі даних. */
    private final UserDaoImpl userDao = new UserDaoImpl();
    /** Список користувачів, що відповідає фільтру пошуку. */
    private final ObservableList<User> users = FXCollections.observableArrayList();
    /** Контейнер для динамічного відображення користувацьких карток. */
    private final VBox userListContainer = new VBox(10);

    /**
     * Конструктор ініціалізує вкладку з пошуком, прокручуваним списком користувачів
     * та підключенням стилів.
     */
    public UserManagementTab() {
        getStylesheets().add(getClass().getResource("/style/user-tab.css").toExternalForm());

        setPadding(new Insets(20));
        setSpacing(15);
        setAlignment(Pos.TOP_CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText(I18n.get("user_search_prompt", "Пошук користувача..."));
        searchField.setMaxWidth(300);
        searchField.getStyleClass().add("text-field");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateUserList(newVal));

        userListContainer.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(userListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");

        getChildren().addAll(searchField, scrollPane);
        updateUserList("");
    }

    /**
     * Оновлює список користувачів, що відповідають вказаному рядку фільтрації.
     *
     * @param filter текст фільтра (імʼя або email користувача)
     */
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

    /**
     * Створює графічну картку користувача з відображенням імені, email,
     * можливістю зміни ролі та кнопкою видалення.
     *
     * @param user обʼєкт {@link User}, який буде відображено у списку
     * @return компонент {@link HBox} з інформацією про користувача
     */
    private HBox createUserCard(User user) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("user-card");

        VBox infoBox = new VBox(5);
        Label name = new Label("👤 " + user.getUsername());
        Label email = new Label("📧 " + user.getEmail());
        name.getStyleClass().add("label");
        email.getStyleClass().add("label");
        infoBox.getChildren().addAll(name, email);

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("USER", "ADMIN");
        roleBox.setValue(user.getRole().toUpperCase());
        roleBox.getStyleClass().add("combo-box");

        roleBox.setOnAction(e -> {
            String selected = roleBox.getValue();
            user.setRole(selected);
            userDao.updateUser(user);
        });

        Button deleteBtn = new Button("🗑 " + I18n.get("delete_user_btn", "Видалити"));
        deleteBtn.getStyleClass().add("button-delete");
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
