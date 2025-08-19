package co.tareq.passwordManager.login;

import co.tareq.passwordManager.MainApp;
import co.tareq.passwordManager.util.PreferenceUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.io.IOException;

import static co.tareq.passwordManager.util.AppConstants.FXML_URI_SETTING_VIEW;

/**
 * Created by Tareq Sefati on 09-Aug-25
 */
public class LoginController {

    private final PreferenceUtil preferenceUtil;

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

    public LoginController(){
        preferenceUtil = new PreferenceUtil();
    }

    @FXML
    public void initialize(){
        checkMongoDbUri();
    }

    @FXML
    void onLoginAction(ActionEvent event) {

    }

    @FXML
    void onRegisterAction(ActionEvent event) {

    }

    @FXML
    void onSettingAction(ActionEvent event) {
        try {
            MainApp.setRoot(FXML_URI_SETTING_VIEW);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Setting Error", "An error occurred during URI setting: " + e.getMessage());
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
