package com.agors.application.form;

import com.agors.application.window.MenuScreen;
import com.agors.infrastructure.persistence.impl.PlaceDaoImpl;
import com.agors.domain.entity.Place;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;

public class GuestForm {

    private FlowPane cardsFlow;
    private List<Place> allPlaces;

    public void start(Stage primaryStage) {
        boolean wasFull = primaryStage.isFullScreen();

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        Button backButton = createBackButton();
        backButton.setOnAction(e -> {
            primaryStage.setFullScreen(wasFull);
            new MenuScreen().show(primaryStage);
        });

        Label titleLabel = new Label("HISTOTREK");
        titleLabel.setFont(Font.font("Arial", 26));
        titleLabel.setTextFill(Color.web("#3e2723"));

        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setMaxWidth(280);
        searchField.setFont(Font.font("Arial", 14));
        searchField.textProperty().addListener((obs, o, n) -> filterPlaces(n));

        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        Button loginButton = createActionButton("LOGIN");
        loginButton.setOnAction(e -> {
            Stage loginStage = new Stage();
            loginStage.setFullScreen(wasFull);
            primaryStage.hide();
            new LoginForm().show(loginStage, primaryStage);
        });

        Button signupButton = createActionButton("SIGNUP");
        signupButton.setOnAction(e -> {
            Stage signupStage = new Stage();
            signupStage.setFullScreen(wasFull);
            primaryStage.hide();
            new SignupForm().show(signupStage, primaryStage);
        });

        HBox authButtons = new HBox(10, loginButton, signupButton);

        topBar.getChildren().addAll(
            backButton,
            titleLabel,
            spacerLeft,
            searchField,
            spacerRight,
            authButtons
        );

        BorderPane mainRoot = new BorderPane();
        mainRoot.setTop(topBar);
        mainRoot.setCenter(wrapCards());

        Pane sandLayer = new Pane();
        sandLayer.setMouseTransparent(true);
        playSandAnimation(sandLayer);

        StackPane root = new StackPane(sandLayer, mainRoot);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #fdf6e3, #e29264);");

        Scene scene = new Scene(root, 800, 600);
        sandLayer.prefWidthProperty().bind(scene.widthProperty());
        sandLayer.prefHeightProperty().bind(scene.heightProperty());

        scene.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });
        primaryStage.setFullScreenExitHint("");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Histotrek");
        primaryStage.setFullScreen(wasFull);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        loadCards();
    }

    private Button createBackButton() {

        StackPane circle = new StackPane();
        circle.setPrefSize(36, 36);
        circle.setStyle(
            "-fx-background-color: white; " +
                "-fx-background-radius: 18; " +
                "-fx-cursor: hand;"
        );
        circle.setEffect(new DropShadow(4, Color.rgb(0,0,0,0.2)));

        Text arrow = new Text("\u2190");
        arrow.setFont(Font.font("Arial", 18));
        arrow.setFill(Color.web("#3e2723"));
        circle.getChildren().add(arrow);

        Button btn = new Button();
        btn.setGraphic(circle);
        btn.setBackground(Background.EMPTY);
        btn.setPadding(Insets.EMPTY);

        ScaleTransition enter = new ScaleTransition(Duration.millis(150), circle);
        enter.setToX(1.1);
        enter.setToY(1.1);
        ScaleTransition exit = new ScaleTransition(Duration.millis(150), circle);
        exit.setToX(1.0);
        exit.setToY(1.0);

        btn.setOnMouseEntered(e -> enter.playFromStart());
        btn.setOnMouseExited(e  -> exit.playFromStart());

        return btn;
    }

    private Button createActionButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", 14));
        btn.setPrefHeight(36);
        btn.setPrefWidth(100);
        btn.setStyle(
            "-fx-background-color: #3e2723; " +
                "-fx-text-fill: #fdf6e3; " +
                "-fx-background-radius: 18; " +
                "-fx-cursor: hand;"
        );
        btn.setEffect(new DropShadow(4, Color.rgb(0,0,0,0.2)));

        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                "-fx-background-color: #5d4037; " +
                    "-fx-text-fill: #fdf6e3; " +
                    "-fx-background-radius: 18; " +
                    "-fx-cursor: hand;"
            );
            ScaleTransition st = new ScaleTransition(Duration.millis(120), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(
                "-fx-background-color: #3e2723; " +
                    "-fx-text-fill: #fdf6e3; " +
                    "-fx-background-radius: 18; " +
                    "-fx-cursor: hand;"
            );
            ScaleTransition st = new ScaleTransition(Duration.millis(120), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        return btn;
    }

    private VBox wrapCards() {
        cardsFlow = new FlowPane(20, 20);
        cardsFlow.setPadding(new Insets(20));
        cardsFlow.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(cardsFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return new VBox(scrollPane);
    }

    private void loadCards() {
        allPlaces = new PlaceDaoImpl().findAll();
        showCards(allPlaces);
    }

    private void showCards(List<Place> places) {
        cardsFlow.getChildren().clear();
        places.forEach(p -> cardsFlow.getChildren().add(createCard(p)));
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
        card.setStyle(
            "-fx-background-color: white; " +
                "-fx-border-color: #d3d3d3; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);"
        );
        card.setPrefWidth(250);
        card.setOnMouseEntered(e -> hoverCard(card, true));
        card.setOnMouseExited(e -> hoverCard(card, false));

        Label title = new Label(place.getName());
        title.setFont(Font.font("Arial", 16));
        title.setTextFill(Color.web("#1a3e2b"));

        Label location = new Label("🌍 " + place.getCountry());
        location.setFont(Font.font("Arial", 12));
        location.setTextFill(Color.web("#555"));

        Label era = new Label("🕰 " + place.getEra());
        era.setFont(Font.font("Arial", 12));
        era.setTextFill(Color.web("#555"));

        Label desc = new Label(place.getDescription());
        desc.setWrapText(true);
        desc.setFont(Font.font("Arial", 12));

        ImageView img = new ImageView();
        img.setFitWidth(230);
        img.setFitHeight(150);
        try {
            img.setImage(new Image(place.getImageUrl(), true));
            card.getChildren().add(img);
        } catch (Exception ex) {
            card.getChildren().add(new Label("Image not available"));
        }

        card.getChildren().addAll(title, location, era, desc);
        return card;
    }

    private void hoverCard(VBox card, boolean hover) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        st.setToX(hover ? 1.05 : 1.0);
        st.setToY(hover ? 1.05 : 1.0);
        st.play();
        card.setStyle(
            "-fx-background-color: " + (hover ? "#fefae0" : "white") + "; " +
                "-fx-border-color: #d3d3d3; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0," + (hover ? "0.2" : "0.1") + "), " +
                (hover ? "8" : "6") + ", 0, 0, " + (hover ? "4" : "2") + ");"
        );
    }

    private void playSandAnimation(Pane sandPane) {
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            double w = sandPane.getWidth(), h = sandPane.getHeight();
            Circle c = new Circle(2, Color.web("#000000", 0.2));
            c.setCenterX(Math.random()*w);
            c.setCenterY(h);
            sandPane.getChildren().add(c);
            TranslateTransition tt = new TranslateTransition(Duration.seconds(4), c);
            tt.setByY(-h);
            tt.setByX(Math.random()*60 - 30);
            tt.setOnFinished(ev -> sandPane.getChildren().remove(c));
            tt.play();
        }));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.setRate(1.5);
        tl.play();
    }
}