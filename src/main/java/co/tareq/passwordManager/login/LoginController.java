package co.tareq.passwordManager.login;

import co.tareq.passwordManager.MainApp;
import co.tareq.passwordManager.model.User;
import co.tareq.passwordManager.service.UserService;
import co.tareq.passwordManager.util.EncryptionUtil;
import co.tareq.passwordManager.util.MongoDBConnection;
import co.tareq.passwordManager.util.PreferenceUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

import static co.tareq.passwordManager.util.AppConstants.*;

/**
 * Created by Tareq Sefati on 09-Aug-25
 */
public class LoginController {
    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXButton btnLogin;

    @FXML
    private JFXButton btnRegister;

    @FXML
    private JFXButton btnSetting;

    private final PreferenceUtil preferenceUtil;
    private final UserService userService;

    public LoginController(){
        preferenceUtil = new PreferenceUtil();
        userService = new UserService();
    }

    @FXML
    public void initialize(){
        checkMongoDbUri();
    }

    @FXML
    void onLoginAction(ActionEvent event) {
        String usernameText = username.getText();
        char[] masterPassword = password.getText().toCharArray(); // Get as char array for security
        if (usernameText.isEmpty() || masterPassword.length == 0) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter username and password.");
            return;
        }
        try {
            User authenticatedUser = userService.authenticateUser(usernameText, masterPassword);

            if (authenticatedUser != null) {
//                System.out.println("Authenticated user: " + authenticatedUser);
                // Derive encryption key from master password
                SecretKey encryptionKey = EncryptionUtil.deriveKeyFromPassword(Arrays.toString(masterPassword),
                        Base64.getDecoder().decode(authenticatedUser.getSalt()));

//                passwordEntryService.setMasterEncryptionKey(encryptionKey);

                // Store current user ID globally or pass it to MainAppController
                // For simplicity, we'll pass it to MainAppController in this example
//                MainAppController.setCurrentUserId(authenticatedUser.getId().toHexString());

                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + usernameText + "!");
                MainApp.setRoot(FXML_APP_DASHBOARD_VIEW); // Navigate to the main application view
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred during login: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clear password from memory immediately after use
            Arrays.fill(masterPassword, ' ');
            password.setText("");
        }
    }

    @FXML
    void onRegisterAction(ActionEvent event) {
        try {
            MainApp.setRoot(FXML_REGISTRATION_VIEW);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "An error occurred during opening Registration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onSettingAction(ActionEvent event) {
        try {
            MainApp.setRoot(FXML_URI_SETTING_VIEW);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Setting Error", "An error occurred during opening URI setting: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkMongoDbUri() {
        if (preferenceUtil.getMongoDbUri().isEmpty()) {
            btnLogin.setDisable(true);
            btnRegister.setDisable(true);
        }else {
            btnLogin.setDisable(false);
            btnRegister.setDisable(false);
            // Important - set mongodb connection string if it exists.
            MongoDBConnection.getInstance().setConnectionString(preferenceUtil.getMongoDbUri());
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
