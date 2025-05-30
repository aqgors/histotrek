package com.agors.application.ui;

import com.agors.domain.entity.Place;
import com.agors.domain.entity.Favorite;
import com.agors.domain.entity.Review;
import com.agors.domain.entity.User;
import com.agors.infrastructure.persistence.impl.PlaceDaoImpl;
import com.agors.infrastructure.persistence.impl.FavoriteDaoImpl;
import com.agors.infrastructure.persistence.impl.ReviewDaoImpl;
import com.agors.infrastructure.persistence.impl.SessionDaoImpl;
import com.agors.infrastructure.persistence.impl.UserDaoImpl;

import com.agors.infrastructure.util.ConnectionHolder;
import com.agors.infrastructure.util.ConnectionManager;
import com.agors.infrastructure.util.SessionContext;
import com.agors.infrastructure.util.ThemeManager;
import com.agors.infrastructure.util.I18n;
import com.agors.domain.enums.ThemeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.animation.*;
import javafx.application.Platform;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.*;
import javafx.util.Duration;

public class UserWindow {

    private final PlaceDaoImpl placeDaoImpl = new PlaceDaoImpl();
    private final FavoriteDaoImpl favoriteDaoImpl = new FavoriteDaoImpl();

    private Label titleLabel;
    private List<Place> allPlaces;
    private int currentUserId;
    private FlowPane allFlow;
    private FlowPane favFlow;
    private Stage primaryStage;
    private Pane sand;
    private StackPane stack;
    private Scene scene;

    public void start(Stage stage, int userId, boolean isFullScreen) {
        this.primaryStage = stage;
        this.currentUserId = userId;
        this.primaryStage.setFullScreen(isFullScreen);
        this.primaryStage.setFullScreenExitHint("");

        allPlaces = placeDaoImpl.findAll();

        HBox topBar = createTopBar();

        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("main-tabpane"); // 🎯 стилізація вкладок

        Tab allTab = new Tab(I18n.get("all_tab", "All")); allTab.setClosable(false);
        Tab favTab = new Tab(I18n.get("favorites_tab", "Favorites")); favTab.setClosable(false);

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

        sand = new Pane(); sand.setMouseTransparent(true);
        stack = new StackPane(sand, root);
        scene = new Scene(stack, 800, 600);

        // 🎨 Підключення стилів
        scene.getStylesheets().add(getClass().getResource("/style/user-window.css").toExternalForm());

        ThemeManager.applyTheme(scene);
        updateTheme();

        sand.prefWidthProperty().bind(scene.widthProperty());
        sand.prefHeightProperty().bind(scene.heightProperty());

        scene.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle(I18n.get("app_title", "Histotrek"));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        ThemeManager.addThemeChangeListener(theme -> {
            updateTheme();
            updateTitleColor();
        });
    }

    private void updateTheme() {
        ThemeType theme = ThemeManager.getCurrentTheme();
        String bgColor;

        switch (theme) {
            case DARK -> bgColor = "#2b2b2b";
            case LIGHT -> bgColor = "white";
            default -> bgColor = "linear-gradient(to bottom right, #fdf6e3, #e29264)";
        }

        if (stack != null) stack.setStyle("-fx-background-color: " + bgColor + ";");

        if (sand != null) {
            sand.getChildren().clear();
            playSandAnimation(sand);
        }
    }

    private void playSandAnimation(Pane pane) {
        Color[] colors = ThemeManager.getSandColors();
        Timeline tl = new Timeline(
            new KeyFrame(Duration.millis(100), e -> {
                Color color = colors[(int)(Math.random() * colors.length)];
                Circle c = new Circle(2, color);
                c.setCenterX(Math.random() * pane.getWidth());
                c.setCenterY(pane.getHeight());
                pane.getChildren().add(c);
                TranslateTransition tt = new TranslateTransition(Duration.seconds(4), c);
                tt.setByY(-pane.getHeight());
                tt.setByX(Math.random() * 60 - 30);
                tt.setOnFinished(ev -> pane.getChildren().remove(c));
                tt.play();
            }));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();
    }

    private HBox createTopBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(10));

        titleLabel = new Label(I18n.get("app_title_user", "HISTOTREK"));
        titleLabel.setFont(Font.font("Arial", 26));
        updateTitleColor();

        TextField search = new TextField();
        search.setId("searchField");
        search.setPromptText(I18n.get("search_prompt_user", "Search..."));
        search.setMaxWidth(280);
        search.setFont(Font.font("Arial", 14));

        Region left = new Region(); HBox.setHgrow(left, Priority.ALWAYS);
        Region right = new Region(); HBox.setHgrow(right, Priority.ALWAYS);

        Button profile = new Button("👤");
        profile.setFont(Font.font(20));
        profile.setCursor(Cursor.HAND);
        profile.getStyleClass().add("avatar-button");

        ContextMenu menu = new ContextMenu();
        MenuItem settings = new MenuItem(I18n.get("settings_user", "Settings"));
        MenuItem logout = new MenuItem(I18n.get("logout", "Logout"));

        settings.setOnAction(e -> {
            Stage settingsStage = new Stage();
            new SettingsWindow(primaryStage).show(settingsStage);
        });

        logout.setOnAction(e -> {
            User currentUser = SessionContext.getCurrentUser();
            if (currentUser != null) {
                try {
                    var conn = ConnectionManager.getConnection();
                    ConnectionHolder.setConnection(conn);
                    new SessionDaoImpl().deleteByUserId(currentUser.getId());
                    conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    ConnectionHolder.clearConnection();
                }
            }

            SessionContext.clear();

            boolean fs = primaryStage.isFullScreen();
            primaryStage.close();
            new MenuScreen().show(primaryStage);
            primaryStage.setFullScreen(fs);
        });

        menu.getItems().addAll(settings, logout);
        profile.setOnMouseClicked(e -> menu.show(profile, Side.BOTTOM, 0, 0));

        bar.getChildren().addAll(titleLabel, left, search, right, profile);
        return bar;
    }

    private void updateTitleColor() {
        if (titleLabel == null) return;

        ThemeType theme = ThemeManager.getCurrentTheme();
        titleLabel.setTextFill(
            theme == ThemeType.DARK ? Color.WHITE : Color.web("#3e2723")
        );
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
        return favoriteDaoImpl.findByUser(currentUserId).stream()
            .map(Favorite::getPlaceId)
            .map(placeDaoImpl::findById)
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
        String baseStyle =
            "-fx-background-color: white; -fx-border-color: #d3d3d3;" +
                "-fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2)";
        card.setStyle(baseStyle);

        try {
            ImageView img = new ImageView(new Image(place.getImageUrl(), true));
            img.setFitWidth(230);
            img.setFitHeight(150);
            card.getChildren().add(img);
        } catch (Exception ignored) {
            card.getChildren().add(new Label(I18n.get("image_not_available_user", "Image not available")));
        }

        Label nameLbl = new Label(place.getName());
        nameLbl.setFont(Font.font(16));
        nameLbl.setTextFill(Color.web("#1a3e2b"));

        ReviewDaoImpl reviewDao = new ReviewDaoImpl();
        List<Review> reviews = reviewDao.findByPlace(place.getId());
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        Label ratingLbl = new Label("★ " + String.format("%.1f", avg));
        ratingLbl.setFont(Font.font("Arial", 13));
        ratingLbl.setTextFill(Color.GOLD);

        VBox infoBox = new VBox(6);
        infoBox.setVisible(false);
        Label locLbl = new Label("🌍 " + place.getCountry());
        Label eraLbl = new Label("🕰 " + place.getEra());
        Label descLbl = new Label(place.getDescription());
        locLbl.setFont(Font.font(12)); locLbl.setTextFill(Color.web("#555"));
        eraLbl.setFont(Font.font(12)); eraLbl.setTextFill(Color.web("#555"));
        descLbl.setWrapText(true); descLbl.setFont(Font.font(12));
        infoBox.getChildren().addAll(locLbl, eraLbl, descLbl);

        card.getChildren().addAll(nameLbl, ratingLbl, infoBox);

        card.setOnMouseEntered(e -> {
            hoverCard(card, true);
            infoBox.setVisible(true);
        });
        card.setOnMouseExited(e -> {
            hoverCard(card, false);
            infoBox.setVisible(false);
        });

        card.setOnMouseClicked(e -> showCardDetails(place));

        return card;
    }

    private void hoverCard(VBox card, boolean hover) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        st.setToX(hover ? 1.05 : 1.0);
        st.setToY(hover ? 1.05 : 1.0);
        st.play();

        String bgColor = hover ? "#fefae0" : "white";
        double opacity = hover ? 0.2 : 0.1;
        int radius = hover ? 8 : 6;
        int blur = hover ? 4 : 2;
        String effect = String.format(
            "dropshadow(gaussian, rgba(0,0,0,%.2f), %d, 0, 0, %d)",
            opacity, radius, blur);

        card.setStyle(
            "-fx-background-color: " + bgColor + ";" +
                "-fx-border-color: #d3d3d3;" +
                "-fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-effect: " + effect + ";");
    }

    private void showCardDetails(Place place) {
        VBox content = new VBox(15);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        ImageView img = new ImageView(new Image(place.getImageUrl(), true));
        img.setFitWidth(400);
        img.setFitHeight(250);

        Label name = new Label(place.getName());
        name.setFont(Font.font("Arial", 22));
        name.setTextFill(Color.web("#1a3e2b"));

        Label country = new Label("🌍 " + (place.getCountry() != null ? place.getCountry() : "—"));
        Label era = new Label("🕰 " + (place.getEra() != null ? place.getEra() : "—"));

        country.setFont(Font.font("Arial", 14));
        country.setTextFill(Color.web("#444"));
        country.setWrapText(true);
        country.setMaxWidth(460);

        era.setFont(Font.font("Arial", 14));
        era.setTextFill(Color.web("#444"));
        era.setWrapText(true);
        era.setMaxWidth(460);

        Text descText = new Text(place.getDescription());
        descText.setFont(Font.font("Arial", 14));
        descText.setFill(Color.web("#444"));
        descText.setWrappingWidth(460);

        TextFlow descFlow = new TextFlow(descText);
        descFlow.setMaxWidth(460);
        descFlow.setLineSpacing(4);

        Label avgRatingLabel = new Label();
        avgRatingLabel.setFont(Font.font("Arial", 14));
        avgRatingLabel.setTextFill(Color.GOLD);

        ReviewDaoImpl reviewDao = new ReviewDaoImpl();
        UserDaoImpl userDao = new UserDaoImpl();
        FavoriteDaoImpl favoriteDao = new FavoriteDaoImpl();

        VBox reviewListBox = new VBox(10);
        reviewListBox.setPadding(new Insets(5));

        ScrollPane contentScroll = new ScrollPane(content);
        contentScroll.setFitToWidth(true);
        contentScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Runnable[] updateReviewsList = new Runnable[1];
        updateReviewsList[0] = () -> {
            UserDaoImpl userDaoForSession = new UserDaoImpl();
            User currentUserNow = userDaoForSession.getUserById(SessionContext.getCurrentUser().getId());
            SessionContext.setCurrentUser(currentUserNow);

            int currentUserIdNow = currentUserNow != null ? currentUserNow.getId() : -1;
            boolean isAdminNow = currentUserNow != null && "ADMIN".equals(currentUserNow.getRole());

            double scrollPos = contentScroll.getVvalue();
            reviewListBox.getChildren().clear();
            List<Review> updated = reviewDao.findByPlace(place.getId());
            double avg = updated.stream().mapToInt(Review::getRating).average().orElse(0.0);
            avgRatingLabel.setText(
                I18n.get("avg_rating_label", "★ Average rating: ") + String.format("%.1f", avg)
            );

            for (Review r : updated) {
                VBox reviewCard = new VBox(4);
                reviewCard.setStyle("-fx-background-color: #f0e9e0; -fx-background-radius: 8;");
                reviewCard.setPadding(new Insets(8));

                String username = userDao.getUserById(r.getUserId()).getUsername();
                Label author = new Label("👤 " + username);
                author.setFont(Font.font("Arial", 13));
                author.setTextFill(Color.BLACK);

                Label rating = new Label("★".repeat(r.getRating()));
                rating.setTextFill(Color.GOLD);
                rating.setFont(Font.font(14));

                Label text = new Label(r.getText());
                text.setWrapText(true);
                text.setFont(Font.font("Arial", 13));
                text.setTextFill(Color.BLACK);

                HBox actions = new HBox(5);
                actions.setAlignment(Pos.CENTER_RIGHT);

                boolean canEdit = r.getUserId() == currentUserIdNow;
                boolean canDelete = canEdit || isAdminNow;

                if (canEdit || canDelete) {
                    if (canEdit) {
                        Button editBtn = new Button(I18n.get("edit_btn", "✏️"));
                        editBtn.setStyle("-fx-background-color: transparent;");
                        editBtn.setOnAction(e -> {
                            TextArea editArea = new TextArea(r.getText());
                            editArea.setWrapText(true);
                            editArea.setFont(Font.font("Arial", 13));

                            HBox editStarBox = new HBox(5);
                            editStarBox.setAlignment(Pos.CENTER_LEFT);
                            int[] editRating = { r.getRating() };
                            for (int i = 1; i <= 5; i++) {
                                Label s = new Label("★");
                                s.setFont(Font.font("Arial", 18));
                                s.setTextFill(i <= editRating[0] ? Color.GOLD : Color.LIGHTGRAY);
                                int starIndex = i;
                                s.setOnMouseEntered(ev -> {
                                    for (int j = 0; j < 5; j++) {
                                        Label star = (Label) editStarBox.getChildren().get(j);
                                        star.setTextFill(j < starIndex ? Color.GOLD : Color.LIGHTGRAY);
                                    }
                                });
                                s.setOnMouseExited(ev -> {
                                    for (int j = 0; j < 5; j++) {
                                        Label star = (Label) editStarBox.getChildren().get(j);
                                        star.setTextFill(j < editRating[0] ? Color.GOLD : Color.LIGHTGRAY);
                                    }
                                });
                                s.setOnMouseClicked(ev -> {
                                    editRating[0] = starIndex;
                                    for (int j = 0; j < 5; j++) {
                                        Label star = (Label) editStarBox.getChildren().get(j);
                                        star.setTextFill(j < starIndex ? Color.GOLD : Color.LIGHTGRAY);
                                    }
                                });
                                editStarBox.getChildren().add(s);
                            }

                            Button saveBtn = new Button(I18n.get("save_btn", "💾 Save"));
                            saveBtn.setStyle("-fx-background-color: #3e2723; -fx-text-fill: white;");
                            saveBtn.setOnAction(ev -> {
                                r.setText(editArea.getText().trim());
                                r.setRating(editRating[0]);
                                r.setCreatedAt(LocalDateTime.now());
                                reviewDao.update(r);
                                updateReviewsList[0].run();
                                Platform.runLater(() -> contentScroll.setVvalue(scrollPos));
                            });
                            reviewCard.getChildren().setAll(author, editStarBox, editArea, saveBtn);
                        });
                        actions.getChildren().add(editBtn);
                    }

                    if (canDelete) {
                        Button delBtn = new Button(I18n.get("delete_btn", "🗑"));
                        delBtn.setStyle("-fx-background-color: transparent;");
                        delBtn.setOnAction(e -> {
                            Stage owner = (Stage) delBtn.getScene().getWindow(); // 👈 отримуємо Stage через кнопку

                            if (MessageBox.showConfirm(
                                I18n.get("confirm_title", "Confirmation"),
                                I18n.get("confirm_delete_review", "Delete this review?"),
                                owner
                            )) {
                                reviewDao.remove(r.getId());
                                updateReviewsList[0].run();
                                Platform.runLater(() -> contentScroll.setVvalue(scrollPos));
                            }
                        });
                        actions.getChildren().add(delBtn);
                    }
                }

                reviewCard.getChildren().addAll(author, rating, text, actions);
                reviewListBox.getChildren().add(reviewCard);
                Platform.runLater(() -> contentScroll.setVvalue(scrollPos));
            }
        };

        updateReviewsList[0].run();

        updateReviewsList[0].run();

        HBox starBox = new HBox(5);
        starBox.setAlignment(Pos.CENTER);
        starBox.setPadding(new Insets(10));
        double[] currentRating = {0};
        for (int i = 1; i <= 5; i++) {
            Label star = new Label("★");
            star.setFont(Font.font("Arial", 24));
            star.setTextFill(Color.LIGHTGRAY);
            int index = i;
            star.setOnMouseEntered(e -> {
                for (int j = 0; j < 5; j++) {
                    starBox.getChildren().get(j).setStyle("-fx-text-fill: " + (j < index ? "gold" : "lightgray"));
                }
            });
            star.setOnMouseExited(e -> {
                for (int j = 0; j < 5; j++) {
                    starBox.getChildren().get(j).setStyle("-fx-text-fill: " + (j < currentRating[0] ? "gold" : "lightgray"));
                }
            });
            star.setOnMouseClicked(e -> currentRating[0] = index);
            starBox.getChildren().add(star);
        }

        TextArea commentArea = new TextArea();
        commentArea.setPromptText(I18n.get("write_review_prompt", "Write your review..."));
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(3);
        commentArea.setMaxWidth(400);
        VBox.setMargin(commentArea, new Insets(0, 0, 10, 0));

        Button sendReview = new Button(I18n.get("send_review_btn", "Send Review"));
        sendReview.setFont(Font.font("Arial", 14));
        sendReview.setStyle("-fx-background-color: #3e2723; -fx-text-fill: white; -fx-background-radius: 8;");
        sendReview.setOnAction(e -> {
            if (currentRating[0] == 0 || commentArea.getText().trim().isEmpty()) {
                MessageBox.show(
                    I18n.get("error_title", "Error"),
                    I18n.get("error_review_fill", "Please rate and fill review text."),
                    ((Stage) ((Button) e.getSource()).getScene().getWindow())
                );
                return;
            }

            Review r = new Review();
            r.setPlaceId(place.getId());
            r.setUserId(currentUserId);
            r.setRating((int) currentRating[0]);
            r.setText(commentArea.getText().trim());
            r.setCreatedAt(LocalDateTime.now());

            reviewDao.add(r);
            commentArea.clear();
            currentRating[0] = 0;
            for (int j = 0; j < 5; j++) {
                starBox.getChildren().get(j).setStyle("-fx-text-fill: lightgray;");
            }

            updateReviewsList[0].run();
        });

        VBox reviewBlock = new VBox(10,
            new Label(I18n.get("rate_place_label", "Rate this place:")), starBox,
            commentArea, sendReview,
            new Label(I18n.get("all_reviews_label", "All reviews:")), reviewListBox
        );
        reviewBlock.setPadding(new Insets(10));
        reviewBlock.setMaxWidth(450);
        reviewBlock.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8;");

        boolean isFav = favoriteDao.isFavorite(currentUserId, place.getId());
        Button favoriteBtn = new Button(isFav
            ? I18n.get("favorite_yes", "✅ In favorites")
            : I18n.get("favorite_no", "Add to favorites ❤"));
        favoriteBtn.setFont(Font.font(14));
        favoriteBtn.setStyle("-fx-background-color: #e29264; -fx-text-fill: white; -fx-background-radius: 8;");
        favoriteBtn.setOnAction(e -> {
            if (favoriteDao.isFavorite(currentUserId, place.getId())) {
                favoriteDao.remove(currentUserId, place.getId());
                favoriteBtn.setText(I18n.get("favorite_no", "Add to favorites ❤"));
                loadCards(favFlow, getFavoritePlaces());
            } else {
                favoriteDao.addToFavorites(currentUserId, place.getId());
                favoriteBtn.setText(I18n.get("favorite_yes", "✅ In favorites"));
                loadCards(favFlow, getFavoritePlaces());
            }
        });

        content.getChildren().addAll(
            img, name, country, era,
            avgRatingLabel, descFlow, favoriteBtn, reviewBlock
        );

        updateReviewsList[0].run();

        Button closeBtn = new Button("✖");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #e29264; -fx-font-size: 18;");
        StackPane.setAlignment(closeBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(closeBtn, new Insets(10));

        StackPane root = new StackPane(contentScroll, closeBtn);
        root.setStyle("-fx-background-color: rgba(0,0,0,0.4);");

        Scene popupScene = new Scene(root);
        popupScene.setFill(Color.TRANSPARENT);
        ThemeManager.applyTheme(popupScene);

        Stage popup = new Stage();
        popup.initOwner(primaryStage);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.setScene(popupScene);
        popup.setResizable(false);

        double popupWidth = primaryStage.getWidth() * 0.8;
        double popupHeight = primaryStage.getHeight() * 0.85;
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);

        content.setMaxWidth(popupWidth - 40);
        content.setMaxHeight(popupHeight - 40);

        popup.setX(primaryStage.getX() + (primaryStage.getWidth() - popupWidth) / 2);
        popup.setY(primaryStage.getY() + (primaryStage.getHeight() - popupHeight) / 2);

        closeBtn.setOnAction(e -> {
            popup.close();
            loadCards(allFlow, allPlaces);
            loadCards(favFlow, getFavoritePlaces());
        });

        content.setScaleX(0.85);
        content.setScaleY(0.85);
        content.setOpacity(0);

        FadeTransition fade = new FadeTransition(Duration.millis(250), content);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        ScaleTransition scale = new ScaleTransition(Duration.millis(250), content);
        scale.setFromX(0.85);
        scale.setFromY(0.85);
        scale.setToX(1);
        scale.setToY(1);
        scale.play();

        popup.showAndWait();
    }
}