package com.agors.application.form;

import com.agors.application.window.MenuScreen;
import com.agors.domain.entity.Place;
import com.agors.domain.entity.Favorite;
import com.agors.infrastructure.persistence.dao.PlaceDao;
import com.agors.infrastructure.persistence.dao.FavoriteDao;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserForm {

    private final PlaceDao placeDao = new PlaceDao();
    private final FavoriteDao favoriteDao = new FavoriteDao();

    private List<Place> allPlaces;
    private int currentUserId;
    private FlowPane allFlow;
    private FlowPane favFlow;

    private Stage primaryStage;

    public void start(Stage primaryStage, int userId) {
        this.primaryStage = primaryStage;
        this.currentUserId = userId;
        allPlaces = placeDao.findAll();

        HBox topBar = createTopBar();

        TabPane tabPane = new TabPane();
        Tab allTab = new Tab("All"); allTab.setClosable(false);
        Tab favTab = new Tab("Favorites"); favTab.setClosable(false);

        allFlow = createFlow();
        favFlow = createFlow();

        allTab.setContent(wrapScroll(allFlow));
        favTab.setContent(wrapScroll(favFlow));
        tabPane.getTabs().addAll(allTab, favTab);

        loadCards(allFlow, allPlaces);
        loadCards(favFlow, getFavoritePlaces());

        TextField searchField = (TextField) topBar.lookup("#searchField");
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            searchField.setText("");
            if (n == allTab) loadCards(allFlow, allPlaces);
            else loadCards(favFlow, getFavoritePlaces());
        });
        searchField.textProperty().addListener((obs, o, n) -> {
            if (tabPane.getSelectionModel().getSelectedItem() == allTab)
                loadCards(allFlow, filterList(allPlaces, n));
            else
                loadCards(favFlow, filterList(getFavoritePlaces(), n));
        });

        BorderPane root = new BorderPane(tabPane);
        root.setTop(topBar);
        Pane sand = new Pane(); sand.setMouseTransparent(true);
        playSandAnimation(sand);
        StackPane stack = new StackPane(sand, root);
        stack.setStyle("-fx-background-color: linear-gradient(to bottom right, #fdf6e3, #e29264);");

        Scene scene = new Scene(stack, 800, 600);
        sand.prefWidthProperty().bind(scene.widthProperty());
        sand.prefHeightProperty().bind(scene.heightProperty());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Histotrek");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private HBox createTopBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(10));

        Label title = new Label("HISTOTREK");
        title.setFont(Font.font("Arial", 26));
        title.setTextFill(Color.web("#3e2723"));

        TextField search = new TextField();
        search.setId("searchField");
        search.setPromptText("Search...");
        search.setMaxWidth(280);
        search.setFont(Font.font("Arial", 14));

        Region left = new Region(); HBox.setHgrow(left, Priority.ALWAYS);
        Region right = new Region(); HBox.setHgrow(right, Priority.ALWAYS);

        Button profile = new Button("👤");
        profile.setFont(Font.font(20));
        profile.setCursor(Cursor.HAND);
        profile.setStyle("-fx-background-color: transparent; -fx-padding: 4;");

        ContextMenu menu = new ContextMenu();
        MenuItem settings = new MenuItem("Settings");
        MenuItem logout = new MenuItem("Logout");
        settings.setOnAction(e -> {/* open settings */});
        logout.setOnAction(e -> {
            primaryStage.hide();
            new MenuScreen().show(primaryStage);
        });
        menu.getItems().addAll(settings, logout);
        profile.setOnMouseClicked(e -> menu.show(profile, Side.BOTTOM, 0, 0));

        bar.getChildren().addAll(title, left, search, right, profile);
        return bar;
    }

    private FlowPane createFlow() {
        FlowPane flow = new FlowPane(20, 20);
        flow.setPadding(new Insets(20));
        flow.setAlignment(Pos.TOP_CENTER);
        return flow;
    }

    private ScrollPane wrapScroll(FlowPane flow) {
        ScrollPane sp = new ScrollPane(flow);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return sp;
    }

    private void loadCards(FlowPane flow, List<Place> places) {
        flow.getChildren().clear();
        places.forEach(p -> flow.getChildren().add(createCard(p)));
    }

    private List<Place> getFavoritePlaces() {
        return favoriteDao.findByUser(currentUserId).stream()
            .map(Favorite::getPlaceId)
            .map(placeDao::findById)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private List<Place> filterList(List<Place> list, String q) {
        String lower = q.toLowerCase();
        return list.stream()
            .filter(p -> p.getName().toLowerCase().contains(lower)
                || p.getCountry().toLowerCase().contains(lower)
                || p.getEra().toLowerCase().contains(lower))
            .collect(Collectors.toList());
    }

    private VBox createCard(Place place) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(250);
        String base = "-fx-background-color: white; -fx-border-color: #d3d3d3; "
            +"-fx-border-radius: 8; -fx-background-radius: 8; "
            +"-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);";
        card.setStyle(base);

        try {
            ImageView img = new ImageView(new Image(place.getImageUrl(), true));
            img.setFitWidth(230);
            img.setFitHeight(150);
            card.getChildren().add(img);
        } catch (Exception ignored) {}

        Label title = new Label(place.getName());
        title.setFont(Font.font(16));
        title.setTextFill(Color.web("#1a3e2b"));

        HBox stars = new HBox(2);
        for (int i = 0; i < 5; i++) {
            Label s = new Label("★");
            s.setFont(Font.font(14));
            s.setTextFill(Color.GOLD);
            stars.getChildren().add(s);
        }
        card.getChildren().addAll(title, stars);

        VBox infoBox = new VBox(6);
        infoBox.setVisible(false);
        Label loc = new Label("🌍 " + place.getCountry()); loc.setFont(Font.font(12)); loc.setTextFill(Color.web("#555"));
        Label era = new Label("🕰 " + place.getEra()); era.setFont(Font.font(12)); era.setTextFill(Color.web("#555"));
        Label desc = new Label(place.getDescription()); desc.setWrapText(true); desc.setFont(Font.font(12));
        infoBox.getChildren().addAll(loc, era, desc);
        card.getChildren().add(infoBox);

        card.setOnMouseEntered(e -> {
            hoverCard(card, true);
            infoBox.setVisible(true);
        });
        card.setOnMouseExited(e -> {
            hoverCard(card, false);
            infoBox.setVisible(false);
        });

        return card;
    }

    private void hoverCard(VBox card, boolean hover) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        st.setToX(hover ? 1.05 : 1.0); st.setToY(hover ? 1.05 : 1.0); st.play();
        String bg = hover ? "#fefae0" : "white";
        double op = hover ? 0.2 : 0.1;
        int rad = hover ? 8 : 6;
        int blur = hover ? 4 : 2;
        card.setStyle(
            "-fx-background-color: " + bg + "; -fx-border-color: #d3d3d3; "
                +"-fx-border-radius: 8; -fx-background-radius: 8; "
                +"-fx-effect: dropshadow(gaussian, rgba(0,0,0," + op + "), " + rad + ",0,0," + blur + ");"
        );
    }

    private void playSandAnimation(Pane pane) {
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            Circle c = new Circle(2, Color.web("#000000", 0.2));
            c.setCenterX(Math.random() * pane.getWidth());
            c.setCenterY(pane.getHeight());
            pane.getChildren().add(c);
            TranslateTransition tt = new TranslateTransition(Duration.seconds(4), c);
            tt.setByY(-pane.getHeight()); tt.setByX(Math.random() * 60 - 30);
            tt.setOnFinished(ev -> pane.getChildren().remove(c)); tt.play();
        }));
        tl.setCycleCount(Timeline.INDEFINITE); tl.play();
    }
}