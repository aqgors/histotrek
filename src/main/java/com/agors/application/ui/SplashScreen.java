package com.agors.application.ui;

import com.agors.infrastructure.util.ConnectionHolder;
import com.agors.infrastructure.util.ConnectionManager;
import com.agors.infrastructure.util.PropertiesUtil;
import com.agors.infrastructure.util.SessionContext;
import com.agors.domain.entity.User;
import com.agors.infrastructure.persistence.impl.SessionDaoImpl;
import com.agors.infrastructure.util.PersistenceInitializer;
import java.sql.Connection;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Optional;

/**
 * Клас, що реалізує екран завантаження (splash screen) для застосунку Histotrek.
 * <p>
 * При відображенні показує анімацію пульсації тексту та падаючого піску,
 * після чого переходить на головне меню або виконує автологін.
 * </p>
 */
public class SplashScreen {

    private final Stage stage;

    public SplashScreen() {
        stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);

        Text text = new Text("HISTOTREK");
        text.setFont(new Font("Arial", 40));
        text.setFill(Color.web("#8B4513"));

        Pane sandPane = new Pane();
        sandPane.setPickOnBounds(false);

        StackPane root = new StackPane(sandPane, text);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #EDC9AF, #e29264);");

        Scene scene = new Scene(root, 500, 350);
        stage.setScene(scene);

        stage.setOnShown(e -> {
            playTextPulse(text);
            playSandAnimation(sandPane);
        });
    }

    /**
     * Відображає splash screen, виконує ініціалізацію БД, а після затримки — відкриває відповідне вікно.
     *
     * @param nextStage основне вікно, яке буде показане
     */
    public void show(Stage nextStage) {
        boolean runDdl = Boolean.parseBoolean(PropertiesUtil.get("db.run.ddl", "false"));
        boolean runDml = Boolean.parseBoolean(PropertiesUtil.get("db.run.dml", "false"));

        if (runDdl || runDml) {
            PersistenceInitializer.init();
        }

        stage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(2.8));
        delay.setOnFinished(e -> {
            stage.close();
            Platform.runLater(() -> {
                try {
                    Connection conn = ConnectionManager.getConnection();
                    ConnectionHolder.setConnection(conn);

                    Optional<User> userOpt = new SessionDaoImpl().findUserByActiveSession();
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        SessionContext.setCurrentUser(user);
                        new UserWindow().start(nextStage, user.getId(), nextStage.isFullScreen());
                    } else {
                        new MenuScreen().show(nextStage);
                    }

                    conn.close();
                    ConnectionHolder.clearConnection();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    new MenuScreen().show(nextStage);
                }
            });
        });
        delay.play();
    }

    private void playTextPulse(Text text) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(1.5), text);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(1.5), text);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        SequentialTransition pulse = new SequentialTransition(scaleUp, scaleDown);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();
    }

    private void playSandAnimation(Pane sandPane) {
        Timeline sandTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            Circle sand = new Circle(2, Color.web("#000000", 0.6));
            sand.setCenterX(Math.random() * 500);
            sand.setCenterY(350);

            sandPane.getChildren().add(sand);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(2), sand);
            tt.setByY(-400);
            tt.setByX(Math.random() * 100 - 50);
            tt.setOnFinished(ev -> sandPane.getChildren().remove(sand));
            tt.play();
        }));
        sandTimeline.setCycleCount(Animation.INDEFINITE);
        sandTimeline.setRate(1.5);
        sandTimeline.play();
    }
}
