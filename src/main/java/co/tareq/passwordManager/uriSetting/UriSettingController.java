package co.tareq.passwordManager.uriSetting;

import co.tareq.passwordManager.MainApp;
import co.tareq.passwordManager.util.MongoDBConnection;
import co.tareq.passwordManager.util.PreferenceUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.prefs.BackingStoreException;

import static co.tareq.passwordManager.util.AppConstants.FXML_LOGIN_VIEW;

/**
 * Created by Tareq Sefati on 11-Aug-25
 */
public class UriSettingController {

    @FXML
    private JFXTextField mongoDbUri;

    @FXML
    private JFXButton btnSaveUri;

    @FXML
    private JFXButton btnTestConnection;

    @FXML
    private JFXButton btnDeleteUri;

    @FXML
    private JFXButton btnBackToLogin;

    private final PreferenceUtil preferenceUtil;

    public UriSettingController(){
        this.preferenceUtil = new PreferenceUtil();
    }

    @FXML
    public void initialize(){
        mongoDbUri.setText(preferenceUtil.getMongoDbUri());
    }

    @FXML
    void onBackToLoginAction(ActionEvent event) {
        try {
            MainApp.setRoot(FXML_LOGIN_VIEW);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred during back to Login UI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onDeleteUriAction(ActionEvent event) {
        try{
            preferenceUtil.deleteMongoDbUri();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Successfully MongoDB URI removed!");
            mongoDbUri.setText("");
            MongoDBConnection.getInstance().setConnectionString(null);
        } catch (BackingStoreException e) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Failed to delete MongoDB URI" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onSaveUriAction(ActionEvent event) {
        String uri = mongoDbUri.getText().trim();
        if (uri.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter the MongoDB Connection URI.");
            return;
        }

        preferenceUtil.saveMongoDbUri(uri);
        MongoDBConnection.getInstance().setConnectionString(uri); // Update the connection in Singleton
        showAlert(Alert.AlertType.INFORMATION, "Settings Saved", "MongoDB Connection URI saved successfully.");
    }

    @FXML
    void onTestConnectionAction(ActionEvent event) {
        String uri = mongoDbUri.getText().trim();
        if (uri.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter the MongoDB Connection URI to test.");
            return;
        }
        try {
            MongoDBConnection.getInstance().setConnectionString(uri);
            // Attempt to get the database. This will trigger a connection attempt.
            MongoDBConnection.getInstance().getDatabase();
            showAlert(Alert.AlertType.INFORMATION, "Connection Test", "Successfully connected to MongoDB!");

            //Set connection string to null after test. Only save operation set connection string and carry on.
            MongoDBConnection.getInstance().setConnectionString(null);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Connection Test Failed", "Failed to connect to MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
