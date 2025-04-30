package com.agors.application.form;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class HistotrekApp {

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

        // Додаємо обробник натискання на кнопку SIGNUP
        signupButton.setOnAction(e -> {
            SignupForm signupForm = new SignupForm();
            signupForm.show(primaryStage);
        });

        HBox buttonBox = new HBox(10, loginButton, signupButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(titleLabel, spacer, buttonBox);
        root.setTop(topBar);

        HBox cardsBox = new HBox(20);
        cardsBox.setAlignment(Pos.CENTER);
        cardsBox.setPadding(new Insets(20));

        for (int i = 0; i < 3; i++) {
            VBox card = createCard(i + 1);
            cardsBox.getChildren().add(card);
        }

        root.setCenter(cardsBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Histotrek");
        primaryStage.show();
    }

    private VBox createCard(int cardNumber) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-background-color: #fff;");

        Label title = new Label("COLOSSEUM");
        title.setFont(new Font("Arial", 16));

        Label location = new Label("Rome, Italy");
        location.setFont(new Font("Arial", 12));

        Label rating = new Label("★★★★★ (18 reviews)");
        rating.setFont(new Font("Arial", 12));

        Label description = new Label("An ancient amphitheater built during the Roman Empire, known for its massive scale and gladiatorial contests. It remains one of Rome's most popular tourist attractions.");
        description.setWrapText(true);
        description.setFont(new Font("Arial", 12));

        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(200);
        try {
            Image image = new Image("https://example.com/colosseum.jpg");
            imageView.setImage(image);
        } catch (Exception e) {
            Label placeholder = new Label("Image not available");
            card.getChildren().add(placeholder);
        }

        card.getChildren().addAll(title, location, rating, description, imageView);
        return card;
    }

}
