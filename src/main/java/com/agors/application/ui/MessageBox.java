package com.agors.application.ui;

import com.agors.infrastructure.util.I18n;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MessageBox {

    public static void show(String title, String message, Stage owner) {
        Stage dialog = createBaseDialog(owner);
        VBox box = createBox();

        Text titleText = createTitle(title);
        Text messageText = createMessage(message);
        Button okButton = createButton(I18n.get("ok"), "#111111");
        okButton.setOnAction(e -> dialog.close());

        box.getChildren().addAll(titleText, messageText, okButton);

        showAnimated(dialog, box);
    }

    public static boolean showConfirm(String title, String message, Stage owner) {
        final boolean[] result = {false};
        Stage dialog = createBaseDialog(owner);
        VBox box = createBox();

        Text titleText = createTitle(title);
        Text messageText = createMessage(message);

        Button yesButton = createButton(I18n.get("yes"), "#111111");
        Button noButton = createButton(I18n.get("cancel"), "#444444");

        yesButton.setOnAction(e -> {
            result[0] = true;
            dialog.close();
        });
        noButton.setOnAction(e -> {
            result[0] = false;
            dialog.close();
        });

        HBox buttons = new HBox(12, yesButton, noButton);
        buttons.setAlignment(Pos.CENTER);

        box.getChildren().addAll(titleText, messageText, buttons);

        showAnimated(dialog, box);
        return result[0];
    }

    private static Stage createBaseDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setResizable(false);
        return dialog;
    }

    private static VBox createBox() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(18));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #3c3f41; -fx-background-radius: 20;");
        return box;
    }

    private static Text createTitle(String text) {
        Text t = new Text(text);
        t.setFont(Font.font("Arial", 18));
        t.setFill(Color.WHITE);
        return t;
    }

    private static Text createMessage(String text) {
        Text t = new Text(text);
        t.setFont(Font.font("Arial", 13));
        t.setFill(Color.WHITE);
        t.setWrappingWidth(260);
        t.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        return t;
    }

    private static Button createButton(String text, String bgColor) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", 13));
        btn.setPrefWidth(90);
        btn.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white; -fx-background-radius: 6;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + bgColor + ", 20%); -fx-text-fill: white; -fx-background-radius: 6;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white; -fx-background-radius: 6;"));
        return btn;
    }

    private static void showAnimated(Stage dialog, VBox box) {
        StackPane root = new StackPane(box);
        root.setStyle("-fx-background-color: transparent");
        StackPane.setAlignment(box, Pos.CENTER);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);

        box.setScaleX(0.85);
        box.setScaleY(0.85);
        box.setOpacity(0);

        FadeTransition fade = new FadeTransition(Duration.millis(220), box);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(220), box);
        scale.setFromX(0.85);
        scale.setFromY(0.85);
        scale.setToX(1);
        scale.setToY(1);

        fade.play();
        scale.play();
        dialog.showAndWait();
    }
}