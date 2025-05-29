package com.agors.application.window;

import com.agors.domain.entity.Place;
import com.agors.domain.validation.PlaceValidator;
import com.agors.infrastructure.persistence.impl.PlaceDaoImpl;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContentManagementTab extends VBox {

    private final ObservableList<Place> places = FXCollections.observableArrayList();
    private final PlaceDaoImpl placeDao = new PlaceDaoImpl();
    private final VBox listContainer = new VBox(10);

    public ContentManagementTab() {
        setPadding(new Insets(20));
        setSpacing(15);
        setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Керування контентом");
        title.setFont(Font.font("Arial", 18));

        TextField searchField = new TextField();
        searchField.setPromptText("Пошук місця...");
        searchField.setMaxWidth(300);

        Button addBtn = new Button("➕ Додати місце");
        addBtn.setOnAction(e -> openAddDialog());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateList(newVal));

        HBox topControls = new HBox(10, searchField, addBtn);
        topControls.setAlignment(Pos.CENTER);

        listContainer.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        getChildren().addAll(title, topControls, scrollPane);
        updateList("");
    }

    private void updateList(String filter) {
        List<Place> all = placeDao.findAll();
        places.setAll(all.stream()
            .filter(p -> p.getName().toLowerCase().contains(filter.toLowerCase()))
            .collect(Collectors.toList()));

        listContainer.getChildren().clear();
        for (Place p : places) {
            listContainer.getChildren().add(createPlaceCard(p));
        }
    }

    private HBox createPlaceCard(Place place) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8;");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(5);
        Label name = new Label("🏛 " + place.getName());
        Label era = new Label("🕰 " + place.getEra());
        name.setFont(Font.font(14));
        era.setFont(Font.font(12));
        name.setStyle("-fx-text-fill: black;");
        era.setStyle("-fx-text-fill: black;");
        info.getChildren().addAll(name, era);

        Button editBtn = new Button("✏️");
        Button deleteBtn = new Button("🗑");

        editBtn.setOnAction(e -> openEditDialog(place));

        deleteBtn.setOnAction(e -> {
            boolean confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Видалити \"" + place.getName() + "\"?",
                ButtonType.YES, ButtonType.NO)
                .showAndWait().filter(ButtonType.YES::equals).isPresent();

            if (confirm) {
                placeDao.remove(place.getId());
                updateList("");
            }
        });

        HBox actions = new HBox(5, editBtn, deleteBtn);
        card.getChildren().addAll(info, actions);
        HBox.setHgrow(info, Priority.ALWAYS);
        return card;
    }

    private Label errorLabel() {
        Label label = new Label();
        label.setTextFill(Color.RED);
        label.setWrapText(true);
        label.setFont(Font.font("Arial", 12));
        return label;
    }

    private void openAddDialog() {
        openPlaceDialog(null);
    }

    private void openEditDialog(Place placeToEdit) {
        openPlaceDialog(placeToEdit);
    }

    private void openPlaceDialog(Place placeToEdit) {
        boolean isEdit = placeToEdit != null;

        Dialog<Place> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Редагувати місце" : "Додати місце");
        dialog.setHeaderText(isEdit ? "Оновіть інформацію про місце" : "Заповніть інформацію про місце");

        TextField nameField = new TextField();
        TextField countryField = new TextField();
        TextField eraField = new TextField();
        TextArea descField = new TextArea();
        TextField imageUrlField = new TextField();

        Label nameError = errorLabel();
        Label countryError = errorLabel();
        Label eraError = errorLabel();
        Label descError = errorLabel();
        Label imageUrlError = errorLabel();

        if (isEdit) {
            nameField.setText(placeToEdit.getName());
            countryField.setText(placeToEdit.getCountry());
            eraField.setText(placeToEdit.getEra());
            descField.setText(placeToEdit.getDescription());
            imageUrlField.setText(placeToEdit.getImageUrl());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Назва:"), 0, 0);         grid.add(nameField, 1, 0); grid.add(nameError, 1, 1);
        grid.add(new Label("Країна:"), 0, 2);        grid.add(countryField, 1, 2); grid.add(countryError, 1, 3);
        grid.add(new Label("Епоха:"), 0, 4);         grid.add(eraField, 1, 4); grid.add(eraError, 1, 5);
        grid.add(new Label("Опис:"), 0, 6);          grid.add(descField, 1, 6); grid.add(descError, 1, 7);
        grid.add(new Label("URL зображення:"), 0, 8); grid.add(imageUrlField, 1, 8); grid.add(imageUrlError, 1, 9);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            Place place = isEdit ? placeToEdit : new Place();
            place.setName(nameField.getText());
            place.setCountry(countryField.getText());
            place.setEra(eraField.getText());
            place.setDescription(descField.getText());
            place.setImageUrl(imageUrlField.getText());

            nameError.setText(""); countryError.setText(""); eraError.setText(""); descError.setText(""); imageUrlError.setText("");

            Map<String, String> errors = new PlaceValidator().validate(place);
            if (!errors.isEmpty()) {
                event.consume();
                nameError.setText(errors.getOrDefault("name", ""));
                countryError.setText(errors.getOrDefault("country", ""));
                eraError.setText(errors.getOrDefault("era", ""));
                descError.setText(errors.getOrDefault("description", ""));
                imageUrlError.setText(errors.getOrDefault("imageUrl", ""));
            } else {
                if (isEdit) {
                    placeDao.update(place);
                } else {
                    placeDao.add(place);
                }
                updateList("");
            }
        });

        dialog.showAndWait();
    }
}
