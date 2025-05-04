package com.agors.application.form;

import com.agors.domain.dao.PlaceDao;
import com.agors.domain.entity.Place;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;

public class GuestForm {

    private FlowPane cardsFlow;
    private List<Place> allPlaces;

    public void start(Stage primaryStage) {
        BorderPane mainRoot = new BorderPane();
        mainRoot.setPadding(new Insets(10));
        mainRoot.setStyle("-fx-background-color: transparent;"); // прозорий центр

        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(10));

        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("HISTOTREK");
        titleLabel.setFont(new Font("Arial", 26));
        titleLabel.setTextFill(Color.web("#3e2723"));

        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setMaxWidth(280);
        searchField.setFont(Font.font("Arial", 14));
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterPlaces(newVal));

        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        Button loginButton = new Button("LOGIN");
        Button signupButton = new Button("SIGNUP");

        loginButton.setOnAction(e -> {
            Stage loginStage = new Stage();
            new LoginForm().show(loginStage, primaryStage);
            primaryStage.hide();
        });

        signupButton.setOnAction(e -> {
            Stage signupStage = new Stage();
            new SignupForm().show(signupStage, primaryStage);
            primaryStage.hide();
        });

        HBox buttons = new HBox(10, loginButton, signupButton);
        topBar.getChildren().addAll(titleLabel, spacerLeft, searchField, spacerRight, buttons);

        topSection.getChildren().add(topBar);
        mainRoot.setTop(topSection);

        cardsFlow = new FlowPane();
        cardsFlow.setPadding(new Insets(20));
        cardsFlow.setHgap(20);
        cardsFlow.setVgap(20);
        cardsFlow.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(cardsFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        mainRoot.setCenter(scrollPane);

        Pane sandLayer = new Pane();
        sandLayer.setMouseTransparent(true);
        playSandAnimation(sandLayer);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f7c280, #e29264);");
        root.getChildren().addAll(sandLayer, mainRoot);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Histotrek");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        loadCards();
    }

    private void loadCards() {
        PlaceDao placeDao = new PlaceDao();
        allPlaces = placeDao.findAll();
        showCards(allPlaces);
    }

    private void showCards(List<Place> places) {
        cardsFlow.getChildren().clear();
        for (Place place : places) {
            cardsFlow.getChildren().add(createCard(place));
        }
    }

    private void filterPlaces(String query) {
        String lower = query.toLowerCase();
        List<Place> filtered = allPlaces.stream()
            .filter(p -> p.getName().toLowerCase().contains(lower)
                || p.getCountry().toLowerCase().contains(lower)
                || p.getEra().toLowerCase().contains(lower))
            .collect(Collectors.toList());
        showCards(filtered);
    }

    private VBox createCard(Place place) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle("-fx-background-color: white; -fx-border-color: #d3d3d3; -fx-border-radius: 8;" +
            "-fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);");
        card.setPrefWidth(250);

        card.setOnMouseEntered(e -> applyHoverZoom(card));
        card.setOnMouseExited(e -> resetZoom(card));

        Label title = new Label(place.getName());
        title.setFont(new Font("Arial", 16));
        title.setTextFill(Color.web("#1a3e2b"));

        Label location = new Label("🌍 " + place.getCountry());
        location.setFont(new Font("Arial", 12));
        location.setTextFill(Color.web("#555"));

        Label era = new Label("🕰 " + place.getEra());
        era.setFont(new Font("Arial", 12));
        era.setTextFill(Color.web("#555"));

        Label description = new Label(place.getDescription());
        description.setWrapText(true);
        description.setFont(new Font("Arial", 12));

        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(230);
        try {
            Image image = new Image(place.getImageUrl(), true);
            imageView.setImage(image);
        } catch (Exception e) {
            Label placeholder = new Label("Image not available");
            card.getChildren().add(placeholder);
        }

        card.getChildren().addAll(imageView, title, location, era, description);
        return card;
    }

    private void applyHoverZoom(VBox card) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        st.setToX(1.05);
        st.setToY(1.05);
        st.play();
        card.setStyle("-fx-background-color: #fefae0; -fx-border-color: #d3d3d3; -fx-border-radius: 8;" +
            "-fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 4);");
    }

    private void resetZoom(VBox card) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
        card.setStyle("-fx-background-color: white; -fx-border-color: #d3d3d3; -fx-border-radius: 8;" +
            "-fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 5, 0, 0, 2);");
    }

    private void playSandAnimation(Pane sandPane) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            double width = sandPane.getWidth();
            double height = sandPane.getHeight();

            Circle particle = new Circle(2, Color.web("#000000", 0.2));
            particle.setCenterX(Math.random() * width);
            particle.setCenterY(height);

            sandPane.getChildren().add(particle);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(4), particle);
            tt.setByY(-height);
            tt.setByX(Math.random() * 60 - 30);
            tt.setOnFinished(ev -> sandPane.getChildren().remove(particle));
            tt.play();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setRate(1.5);
        timeline.play();
    }
}