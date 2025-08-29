package co.tareq.passwordManager.util;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Created by Tareq Sefati on 29-Aug-25
 */
public class Toast {

    public static void show(Stage ownerStage, String message, int durationMs) {
        Stage toastStage = new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.setResizable(false);
        toastStage.initModality(Modality.NONE); // Allow interaction with owner stage
        toastStage.initStyle(StageStyle.TRANSPARENT); // No window decorations

        Label toastLabel = new Label(message);
        toastLabel.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.7);" + // Semi-transparent black background
                        "-fx-text-fill: white;" +                   // White text
                        "-fx-padding: 10px;" +                      // Padding around text
                        "-fx-font-size: 14px;" +                    // Font size
                        "-fx-background-radius: 5px;"               // Rounded corners
        );
        toastLabel.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(toastLabel);
        root.setStyle("-fx-background-color: transparent;"); // Make root transparent
        root.setPadding(new Insets(20)); // Padding for positioning

        Scene scene = new Scene(root, Color.TRANSPARENT);
        toastStage.setScene(scene);

        // Position the toast relative to the owner stage (e.g., center-bottom)
        toastStage.setX(ownerStage.getX() + ownerStage.getWidth() / 2 - scene.getWidth() / 2);
        toastStage.setY(ownerStage.getY() + ownerStage.getHeight() - 80); // 80 pixels from bottom

        toastStage.show();

        // Animate the toast to fade out
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(durationMs)); // Delay before fading out
        fadeOut.setOnFinished(e -> toastStage.close()); // Close stage after fade out
        fadeOut.play();
    }
}
