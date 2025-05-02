package com.agors.application.form;

import com.agors.domain.dao.PlaceDao;
import com.agors.domain.entity.Place;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.util.List;

public class GuestForm {

    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #f0f0f0;");

        Label titleLabel = new Label("HISTOTREK");
        titleLabel.setFont(new Font("Arial", 24));

        Button loginButton = new Button("LOGIN");
        Button signupButton = new Button("SIGNUP");

        signupButton.setOnAction(e -> {
            SignupForm signupForm = new SignupForm();
            Stage signupStage = new Stage();
            signupForm.show(signupStage, primaryStage);
            primaryStage.hide();
        });

        loginButton.setOnAction(e -> {
            Stage loginStage = new Stage();
            new LoginForm().show(loginStage, primaryStage);
            primaryStage.hide();
        });

        HBox buttonBox = new HBox(10, loginButton, signupButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(titleLabel, spacer, buttonBox);
        root.setTop(topBar);

        GridPane cardsGrid = new GridPane();
        cardsGrid.setPadding(new Insets(20));
        cardsGrid.setHgap(20);
        cardsGrid.setVgap(20);

        PlaceDao placeDao = new PlaceDao();
        List<Place> places = placeDao.findAll();

        int column = 0;
        int row = 0;

        for (Place place : places) {
            VBox card = createCard(place);
            cardsGrid.add(card, column, row);

            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }

        ScrollPane scrollPane = new ScrollPane(cardsGrid);
        scrollPane.setFitToWidth(true);

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Histotrek");

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        primaryStage.show();
    }

    private VBox createCard(Place place) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-background-color: #fff; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        card.setPrefWidth(220);

        card.setOnMouseEntered(e -> applyHoverZoom(card));
        card.setOnMouseExited(e -> resetZoom(card));

        Label title = new Label(place.getName());
        title.setFont(new Font("Arial", 16));

        Label location = new Label(place.getCountry());
        location.setFont(new Font("Arial", 12));

        Label era = new Label(place.getEra());
        era.setFont(new Font("Arial", 12));

        Label description = new Label(place.getDescription());
        description.setWrapText(true);
        description.setFont(new Font("Arial", 12));

        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(200);
        try {
            Image image = new Image(place.getImageUrl(), true);
            imageView.setImage(image);
        } catch (Exception e) {
            Label placeholder = new Label("Image not available");
            card.getChildren().add(placeholder);
        }

        card.getChildren().addAll(title, location, era, description, imageView);
        return card;
    }

    private void applyHoverZoom(VBox card) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), card);
        scaleTransition.setToX(1.05);
        scaleTransition.setToY(1.05);
        scaleTransition.play();
        card.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #d3d3d3; -fx-border-width: 1; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 5);");
    }

    private void resetZoom(VBox card) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), card);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.play();
        card.setStyle("-fx-background-color: #fff; -fx-border-color: #d3d3d3; -fx-border-width: 1; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");
    }
}