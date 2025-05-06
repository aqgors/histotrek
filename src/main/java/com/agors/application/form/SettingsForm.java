package com.agors.application.form;

import com.agors.application.window.MenuScreen;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SettingsForm {

    private final Stage parentStage;

    public SettingsForm(Stage parentStage) {
        this.parentStage = parentStage;
    }

    public void show(Stage stage) {
        parentStage.close();

        Button backBtn = createBackButton();
        backBtn.setOnAction(e -> {
            stage.close();
            parentStage.show();
        });

        Text title = new Text("Settings");
        title.setFont(Font.font("Arial", 28));
        title.setFill(Color.BLACK);

        HBox topBar = new HBox(10, backBtn, title);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: white; -fx-border-color: #d3d3d3;");

        VBox content = new VBox(20,
            createSection("Profile",
                createStyledButton("Змінити ім'я користувача"),
                createStyledButton("Оновити електронну пошту"),
                createStyledButton("Змінити пароль")
            ),
            createSection("Preferences",
                createThemeToggle(),
                createLanguageChoice(),
                createFontSizeControl()
            ),
            createSection("Administration",
                createStyledButton("Access the Citadel", e -> {
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Administration");
                    info.setHeaderText(null);
                    info.setContentText("Admin menu coming soon.");
                    info.showAndWait();
                })
            ),
            createSection("Privacy & Security",
                createStyledCheckBox("Двохфакторна автентифікація"),
                createStyledButton("Видалити обліковий запис")
            ),
            createSection("About",
                createStyledLabel("Версія додатку: 1.0.0"),
                createStyledLink("Ліцензія / Умови використання"),
                createStyledLink("Контакти підтримки")
            ),
            createSection("Help & Feedback",
                createStyledLink("Часті питання"),
                createStyledButton("Надіслати відгук"),
                createStyledLink("Зв’язатися з підтримкою")
            )
        );
        content.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(scrollPane);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #fdf6e3, #e29264);");

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Settings");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    private VBox createSection(String heading, javafx.scene.Node... controls) {
        Text h = new Text(heading);
        h.setFont(Font.font("Arial", 20));
        h.setFill(Color.BLACK);
        VBox box = new VBox(10);
        box.getChildren().add(h);
        for (javafx.scene.Node c : controls) {
            box.getChildren().add(c);
        }
        box.setPadding(new Insets(10));
        box.setStyle(
            "-fx-background-color: white; " +
                "-fx-border-color: #d3d3d3; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;"
        );
        return box;
    }

    private Button createStyledButton(String text) {
        return createStyledButton(text, null);
    }

    private Button createStyledButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", 14));
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #c2b280; -fx-text-fill: black; -fx-background-radius: 8;");
        btn.setCursor(Cursor.HAND);
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #a99e75; -fx-text-fill: black; -fx-background-radius: 8;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #c2b280; -fx-text-fill: black; -fx-background-radius: 8;"));
        btn.setEffect(new DropShadow(4, Color.rgb(0,0,0,0.2)));
        if (handler != null) btn.setOnAction(handler);
        return btn;
    }

    private CheckBox createStyledCheckBox(String text) {
        CheckBox cb = new CheckBox(text);
        cb.setFont(Font.font("Arial", 14));
        cb.setTextFill(Color.BLACK);
        return cb;
    }

    private HBox createThemeToggle() {
        Label lbl = new Label("Theme:");
        lbl.setFont(Font.font("Arial", 14));
        lbl.setTextFill(Color.BLACK);

        ToggleButton toggle = new ToggleButton("Dark");
        toggle.setFont(Font.font("Arial", 14));
        toggle.setCursor(Cursor.HAND);
        toggle.setStyle("-fx-background-color: #c2b280; -fx-text-fill: black; -fx-background-radius: 12;");
        toggle.setOnAction(e -> {
            Scene s = toggle.getScene();
            if (toggle.isSelected()) {
                toggle.setText("Light");
                s.getRoot().setStyle("-fx-background-color: #2b2b2b;");
            } else {
                toggle.setText("Dark");
                s.getRoot().setStyle("-fx-background-color: linear-gradient(to bottom right, #fdf6e3, #e29264);");
            }
        });

        HBox box = new HBox(10, lbl, toggle);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private ChoiceBox<String> createLanguageChoice() {
        ChoiceBox<String> cb = new ChoiceBox<>();
        cb.getItems().addAll("Українська", "English");
        cb.setValue("Українська");
        cb.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        return cb;
    }

    private HBox createFontSizeControl() {
        Label lbl = new Label("Font Size:");
        lbl.setFont(Font.font("Arial", 14));
        lbl.setTextFill(Color.BLACK);

        Slider slider = new Slider(10, 24, 14);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(2);
        slider.setMinorTickCount(1);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);
        slider.setPrefWidth(200);

        Label valueLabel = new Label(String.valueOf((int) slider.getValue()));
        valueLabel.setFont(Font.font("Arial", 14));
        valueLabel.setTextFill(Color.BLACK);

        slider.valueProperty().addListener((obs, o, n) -> {
            int val = n.intValue();
            valueLabel.setText(String.valueOf(val));
            // TODO: apply font size across the application
        });

        HBox box = new HBox(10, lbl, slider, valueLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private Label createStyledLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", 14));
        l.setTextFill(Color.BLACK);
        return l;
    }

    private Hyperlink createStyledLink(String text) {
        Hyperlink hl = new Hyperlink(text);
        hl.setFont(Font.font("Arial", 14));
        hl.setTextFill(Color.BLACK);
        return hl;
    }

    private Button createBackButton() {
        StackPane circle = new StackPane();
        circle.setPrefSize(36, 36);
        circle.setStyle("-fx-background-color: white; -fx-background-radius: 18;");
        circle.setEffect(new DropShadow(4, Color.rgb(0,0,0,0.2)));

        Text arrow = new Text("\u2190");
        arrow.setFont(Font.font("Arial", 18));
        arrow.setFill(Color.web("#3e2723"));
        circle.getChildren().add(arrow);

        Button btn = new Button();
        btn.setGraphic(circle);
        btn.setBackground(Background.EMPTY);
        btn.setCursor(Cursor.HAND);

        ScaleTransition enter = new ScaleTransition(Duration.millis(150), circle);
        enter.setToX(1.1); enter.setToY(1.1);
        ScaleTransition exit = new ScaleTransition(Duration.millis(150), circle);
        exit.setToX(1.0); exit.setToY(1.0);
        btn.setOnMouseEntered(e -> enter.playFromStart());
        btn.setOnMouseExited(e -> exit.playFromStart());

        return btn;
    }
}