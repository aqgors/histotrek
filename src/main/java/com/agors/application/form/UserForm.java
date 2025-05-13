package com.agors.application.form;

import com.agors.application.window.MenuScreen;
import com.agors.application.form.SettingsForm;
import com.agors.domain.entity.Place;
import com.agors.domain.entity.Favorite;
import com.agors.infrastructure.persistence.impl.PlaceDaoImpl;
import com.agors.infrastructure.persistence.impl.FavoriteDaoImpl;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Форма користувача: відображає список усіх історичних місць
 * та обраних місць, дозволяє пошук, перегляд деталей та навігацію
 * до налаштувань або виходу.
 *
 * @author agors
 * @version 1.0
 */
public class UserForm {

    private final PlaceDaoImpl placeDaoImpl = new PlaceDaoImpl();
    private final FavoriteDaoImpl favoriteDaoImpl = new FavoriteDaoImpl();

    private List<Place> allPlaces;
    private int currentUserId;
    private FlowPane allFlow;
    private FlowPane favFlow;
    private Stage primaryStage;

    /**
     * Ініціалізує і показує форму користувача.
     *
     * @param stage        головна сцена програми
     * @param userId       ідентифікатор поточного користувача
     * @param isFullScreen прапорець, чи відкривати у повноекранному режимі
     */
    public void start(Stage stage, int userId, boolean isFullScreen) {
        this.primaryStage  = stage;
        this.currentUserId = userId;
        this.primaryStage.setFullScreen(isFullScreen);
        this.primaryStage.setFullScreenExitHint("");

        // Завантажуємо всі місця
        allPlaces = placeDaoImpl.findAll();

        HBox topBar = createTopBar();

        // Вкладки: всі місця та обрані
        TabPane tabPane = new TabPane();
        Tab allTab = new Tab("All");       allTab.setClosable(false);
        Tab favTab = new Tab("Favorites"); favTab.setClosable(false);

        allFlow = createFlow();
        favFlow = createFlow();

        allTab.setContent(wrapScroll(allFlow));
        favTab.setContent(wrapScroll(favFlow));
        tabPane.getTabs().addAll(allTab, favTab);

        // Першочергова завантаження карток
        loadCards(allFlow, allPlaces);
        loadCards(favFlow, getFavoritePlaces());

        // Пошук у вкладках
        TextField searchField = (TextField) topBar.lookup("#searchField");
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            searchField.setText("");
            if (n == allTab) loadCards(allFlow, allPlaces);
            else              loadCards(favFlow, getFavoritePlaces());
        });
        searchField.textProperty().addListener((obs, o, n) -> {
            if (tabPane.getSelectionModel().getSelectedItem() == allTab)
                loadCards(allFlow, filterList(allPlaces, n));
            else
                loadCards(favFlow, filterList(getFavoritePlaces(), n));
        });

        BorderPane root = new BorderPane(tabPane);
        root.setTop(topBar);

        // Фонова анімація піску
        Pane sand = new Pane(); sand.setMouseTransparent(true);
        playSandAnimation(sand);
        StackPane stack = new StackPane(sand, root);
        stack.setStyle("-fx-background-color: linear-gradient(to bottom right, #fdf6e3, #e29264);");

        Scene scene = new Scene(stack, 800, 600);
        sand.prefWidthProperty().bind(scene.widthProperty());
        sand.prefHeightProperty().bind(scene.heightProperty());

        // F11 для повноекранного режиму
        scene.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Histotrek");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    /**
     * Створює панель зверху з назвою, полем пошуку та кнопкою профілю.
     */
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

        Region left = new Region();  HBox.setHgrow(left, Priority.ALWAYS);
        Region right = new Region(); HBox.setHgrow(right, Priority.ALWAYS);

        Button profile = new Button("👤");
        profile.setFont(Font.font(20));
        profile.setCursor(Cursor.HAND);
        profile.setStyle("-fx-background-color: transparent; -fx-padding: 4;");

        ContextMenu menu = new ContextMenu();
        MenuItem settings = new MenuItem("Settings");
        MenuItem logout   = new MenuItem("Logout");
        settings.setOnAction(e -> {
            Stage settingsStage = new Stage();
            new SettingsForm(primaryStage).show(settingsStage);
        });
        logout.setOnAction(e -> {
            boolean fs = primaryStage.isFullScreen();
            primaryStage.close();
            new MenuScreen().show(primaryStage);
            primaryStage.setFullScreen(fs);
        });
        menu.getItems().addAll(settings, logout);
        profile.setOnMouseClicked(e -> menu.show(profile, Side.BOTTOM, 0, 0));

        bar.getChildren().addAll(title, left, search, right, profile);
        return bar;
    }

    /**
     * Створює FlowPane для карток.
     */
    private FlowPane createFlow() {
        FlowPane flow = new FlowPane(20, 20);
        flow.setPadding(new Insets(20));
        flow.setAlignment(Pos.TOP_CENTER);
        return flow;
    }

    /**
     * Обгортає FlowPane у ScrollPane.
     */
    private ScrollPane wrapScroll(FlowPane flow) {
        ScrollPane sp = new ScrollPane(flow);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return sp;
    }

    /**
     * Завантажує картки місць у вказаний FlowPane.
     */
    private void loadCards(FlowPane flow, List<Place> places) {
        flow.getChildren().clear();
        places.forEach(p -> flow.getChildren().add(createCard(p)));
    }

    /**
     * Повертає список улюблених місць поточного користувача.
     */
    private List<Place> getFavoritePlaces() {
        return favoriteDaoImpl.findByUser(currentUserId).stream()
            .map(Favorite::getPlaceId)
            .map(placeDaoImpl::findById)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Фільтрує список місць за запитом користувача.
     */
    private List<Place> filterList(List<Place> list, String q) {
        String lower = q.toLowerCase();
        return list.stream()
            .filter(p -> p.getName().toLowerCase().contains(lower)
                || p.getCountry().toLowerCase().contains(lower)
                || p.getEra().toLowerCase().contains(lower))
            .collect(Collectors.toList());
    }

    /**
     * Створює картку із інформацією про місце.
     */
    private VBox createCard(Place place) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(250);
        String baseStyle =
            "-fx-background-color: white; -fx-border-color: #d3d3d3;"
                + "-fx-border-radius: 8; -fx-background-radius: 8;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2)";
        card.setStyle(baseStyle);

        try {
            ImageView img = new ImageView(new Image(place.getImageUrl(), true));
            img.setFitWidth(230);
            img.setFitHeight(150);
            card.getChildren().add(img);
        } catch (Exception ignored) {}

        Label nameLbl = new Label(place.getName());
        nameLbl.setFont(Font.font(16));
        nameLbl.setTextFill(Color.web("#1a3e2b"));

        HBox stars = new HBox(2);
        for (int i = 0; i < 5; i++) {
            Label s = new Label("★");
            s.setFont(Font.font(14));
            s.setTextFill(Color.GOLD);
            stars.getChildren().add(s);
        }

        VBox infoBox = new VBox(6);
        infoBox.setVisible(false);
        Label locLbl  = new Label("🌍 " + place.getCountry());
        Label eraLbl  = new Label("🕰 " + place.getEra());
        Label descLbl = new Label(place.getDescription());
        locLbl .setFont(Font.font(12)); locLbl .setTextFill(Color.web("#555"));
        eraLbl .setFont(Font.font(12)); eraLbl .setTextFill(Color.web("#555"));
        descLbl.setWrapText(true); descLbl.setFont(Font.font(12));
        infoBox.getChildren().addAll(locLbl, eraLbl, descLbl);

        card.getChildren().addAll(nameLbl, stars, infoBox);
        card.setOnMouseEntered(e -> { hoverCard(card, true);  infoBox.setVisible(true); });
        card.setOnMouseExited (e -> { hoverCard(card, false); infoBox.setVisible(false); });

        return card;
    }

    /**
     * Анімація наведення на картку: змінює масштаб і тінь.
     */
    private void hoverCard(VBox card, boolean hover) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        st.setToX(hover ? 1.05 : 1.0);
        st.setToY(hover ? 1.05 : 1.0);
        st.play();

        String bgColor = hover ? "#fefae0" : "white";
        double opacity   = hover ? 0.2      : 0.1;
        int    radius    = hover ? 8        : 6;
        int    blur      = hover ? 4        : 2;
        String effect = String.format(
            "dropshadow(gaussian, rgba(0,0,0,%.2f), %d, 0, 0, %d)",
            opacity, radius, blur);

        card.setStyle(
            "-fx-background-color: " + bgColor + ";"
                + "-fx-border-color: #d3d3d3;"
                + "-fx-border-radius: 8; -fx-background-radius: 8;"
                + "-fx-effect: " + effect + ";");
    }

    /**
     * Фонова анімація падаючого піску.
     */
    private void playSandAnimation(Pane pane) {
        Timeline tl = new Timeline(
            new KeyFrame(Duration.millis(100), e -> {
                Circle c = new Circle(2, Color.web("#000000", 0.2));
                c.setCenterX(Math.random() * pane.getWidth());
                c.setCenterY(pane.getHeight());
                pane.getChildren().add(c);
                TranslateTransition tt = new TranslateTransition(
                    Duration.seconds(4), c);
                tt.setByY(-pane.getHeight());
                tt.setByX(Math.random() * 60 - 30);
                tt.setOnFinished(ev -> pane.getChildren().remove(c));
                tt.play();
            }));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();
    }
}
