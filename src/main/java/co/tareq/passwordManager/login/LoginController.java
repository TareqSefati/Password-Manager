package co.tareq.passwordManager.login;

import co.tareq.passwordManager.MainApp;
import co.tareq.passwordManager.dashboard.AppDashboardController;
import co.tareq.passwordManager.model.User;
import co.tareq.passwordManager.service.PasswordEntryService;
import co.tareq.passwordManager.service.UserService;
import co.tareq.passwordManager.util.EncryptionUtil;
import co.tareq.passwordManager.util.MongoDBConnection;
import co.tareq.passwordManager.util.PreferenceUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @FXML
    private ProgressIndicator progressIndicator;

    private final PreferenceUtil preferenceUtil;
    private final UserService userService;
    private final PasswordEntryService passwordEntryService;

    // Executor Service for managing background database operations (Fixed thread pool of 1)
    private final ExecutorService executor = Executors.newFixedThreadPool(1, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true); // Ensures the thread won't block application shutdown
        t.setName("DB-Task-Worker");
        return t;
    });

    public void shutdownExecutor() {
        executor.shutdownNow();
        System.out.println("Is executor shutdown: "+ executor.isShutdown());
    }

    public LoginController(){
        preferenceUtil = new PreferenceUtil();
        userService = new UserService();
        passwordEntryService = new PasswordEntryService();
    }

    @FXML
    public void initialize(){
        checkMongoDbUri();
        progressIndicator.setVisible(false);
    }

    @FXML
    void onLoginAction(ActionEvent event) {
        String usernameText = username.getText();
        char[] masterPassword = password.getText().toCharArray(); // Get as char array for security
        if (usernameText.isEmpty() || masterPassword.length == 0) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter username and password.");
            return;
        }

        //Prepare UI for loading state
        setUIState(true);

        //Create the Task for background work
        Task<User> loginTask = new Task<>() {
            private SecretKey encryptionKey = null;
            @Override
            protected User call() throws Exception {
                User authenticatedUser = userService.authenticateUser(usernameText, masterPassword);
                if (authenticatedUser != null) {
                    // Derive encryption key from master password
                    encryptionKey = EncryptionUtil.deriveKeyFromPassword(Arrays.toString(masterPassword),
                            Base64.getDecoder().decode(authenticatedUser.getSalt()));
                }
                return authenticatedUser;
            }

            @Override
            protected void succeeded() {
                User authenticatedUser = getValue();
                if (authenticatedUser != null && encryptionKey != null) {
                    try {
                        // Set application state
                        // Store current user ID globally or pass it to MainAppController
                        // For simplicity, we'll pass it to MainAppController in this example
                        passwordEntryService.setMasterEncryptionKey(encryptionKey);
                        AppDashboardController.setCurrentUser(authenticatedUser);

                        showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + usernameText + "!");
                        MainApp.setRoot(FXML_APP_DASHBOARD_VIEW); // Navigate to dashboard
                        // Shutdown executor to avoid Resource Leak
                        shutdownExecutor();
                    } catch (IOException e) {
                        showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load dashboard view.");
                        e.printStackTrace();
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
                }
            }
            @Override
            protected void failed() {
                //ERROR HANDLING (JavaFX Application Thread)
                Throwable e = getException();
                showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred during login: " + e.getMessage());
                e.printStackTrace();
            }
            @Override
            protected void done() {
                //FINAL CLEANUP (Always runs) ---
                setUIState(false); // Hide spinner, re-enable UI
                // Clear password from memory immediately after use
                Arrays.fill(masterPassword, ' ');
            }
        };
        executor.execute(loginTask);
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
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Manages the state of the UI components (locking them during the task).
     * @param isLoading true if the task is running; false otherwise.
     */
    private void setUIState(boolean isLoading) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(isLoading);
            btnLogin.setDisable(isLoading);
            username.setDisable(isLoading);
            password.setDisable(isLoading);
            btnRegister.setDisable(isLoading);
            btnSetting.setDisable(isLoading);
        });
    }
}
