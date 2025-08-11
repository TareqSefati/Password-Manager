package co.tareq.passwordManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static co.tareq.passwordManager.util.AppConstants.APP_TITLE;
import static co.tareq.passwordManager.util.AppConstants.FXML_LOGIN_VIEW;

public class MainApp extends Application {

    private static Stage primaryStage; // To manage the main stage
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle(APP_TITLE);
        // Load the Login view initially
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_LOGIN_VIEW));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root); // Adjust size as needed
//        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void setRoot(String fxmlWithFullPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxmlWithFullPath));
        Parent root = fxmlLoader.load();
        primaryStage.setResizable(true);
        primaryStage.setScene(new Scene(root));
//        primaryStage.getScene().getStylesheets().add(Objects.requireNonNull(MainApp.class.getResource("style.css")).toExternalForm());
        primaryStage.sizeToScene(); // Adjust stage size to content
        primaryStage.centerOnScreen(); // Center the stage
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}