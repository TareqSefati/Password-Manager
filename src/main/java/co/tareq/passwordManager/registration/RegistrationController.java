package co.tareq.passwordManager.registration;

import co.tareq.passwordManager.MainApp;
import co.tareq.passwordManager.service.UserService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.regex.Pattern;

import static co.tareq.passwordManager.util.AppConstants.FXML_LOGIN_VIEW;

/**
 * Created by Tareq Sefati on 19-Aug-25
 */
public class RegistrationController {
    @FXML
    private JFXTextField username;

    @FXML
    private Label lblUsernameError;

    @FXML
    private JFXTextField email;

    @FXML
    private Label lblEmailError;

    @FXML
    private JFXPasswordField password;

    @FXML
    private Label lblPasswordError;

    @FXML
    private JFXPasswordField confirmPassword;

    @FXML
    private Label lblConfirmPasswordError;

    @FXML
    private JFXButton btnRegister;

    @FXML
    private JFXButton btnClearFields;

    @FXML
    private JFXButton btnBackToLogin;

    // Registration Model User instance
    private RegUser user = new RegUser();
    private final UserService userService;

    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    // Password: At least 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$");

    // Individual field validity properties
    private BooleanProperty isUsernameValid = new SimpleBooleanProperty(false);
    private BooleanProperty isEmailValid = new SimpleBooleanProperty(false);
    private BooleanProperty isPasswordValid = new SimpleBooleanProperty(false);
    private BooleanProperty isConfirmPasswordValid = new SimpleBooleanProperty(false);

    public RegistrationController() {
        this.userService = new UserService();
    }

    @FXML
    public void initialize() {
        // Bind model properties to UI fields (two-way binding)
//        username.textProperty().bindBidirectional(user.usernameProperty());
//        email.textProperty().bindBidirectional(user.emailProperty());
//        password.textProperty().bindBidirectional(user.passwordProperty());
//        confirmPasswordField.textProperty().bindBidirectional(user.confirmPasswordProperty());

        // --- Add Listeners for Field-by-Field Validation ---

        // Username validation
        username.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // When field loses focus
//                isUsernameValid.set(validateUsername());
//                lblUsernameError.setText("validation username");
//                validateUsername();
                isUsernameValid.set(validateUsername());
            }else {
                clearErrorLabel(lblUsernameError);
            }
        });
//        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
//            // Optional: Validate instantly as user types (can be noisy for some validations)
//            // isUsernameValid.set(validateUsername());
//        });

        // Email validation
        email.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // When field loses focus
//                isEmailValid.set(validateEmail());
//                validateEmail();
                isEmailValid.set(validateEmail());
            }else {
                clearErrorLabel(lblEmailError);
            }
        });
//        // Optional real-time email validation:
//        // emailField.textProperty().addListener((obs, oldVal, newVal) -> isEmailValid.set(validateEmail()));
//
//
        // Password validation
        password.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // When field loses focus
//                isPasswordValid.set(validatePassword());
//                validatePassword();
                isPasswordValid.set(validatePassword());
            }else {
                clearErrorLabel(lblPasswordError);
            }
        });
        // Optional real-time password validation:
//         password.textProperty()
//                 .addListener((obs, oldVal, newVal) -> {
////                     isPasswordValid.set(validatePassword());
//                     validatePassword();
//                 });

//
        // Confirm Password validation
//        confirmPassword.focusedProperty().addListener((obs, oldVal, newVal) -> {
//            if (!newVal) { // When field loses focus
////                isConfirmPasswordValid.set(validateConfirmPassword());
//                validateConfirmPassword();
//            }else {
//                clearErrorLabel(lblConfirmPasswordError);
//                validateConfirmPassword();
//            }
////            isConfirmPasswordValid.set(validateConfirmPassword());
////            validateConfirmPassword();
//        });
        confirmPassword.textProperty().addListener((observableValue, oldVal, newVal) -> {
            if(!newVal.isEmpty()) {
//                validateConfirmPassword();
                isConfirmPasswordValid.set(validateConfirmPassword());
            }else {
                clearErrorLabel(lblConfirmPasswordError);
            }
        });
//        // Optional real-time confirm password validation:
//        // confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> isConfirmPasswordValid.set(validateConfirmPassword()));
//
//
        // --- Overall Form Validity Binding ---
        BooleanBinding overallFormValid = isUsernameValid
                .and(isEmailValid)
                .and(isPasswordValid)
                .and(isConfirmPasswordValid);

        btnRegister.disableProperty().bind(overallFormValid.not());
    }

    @FXML
    void onClearFieldsAction(ActionEvent event) {
        clearAllInputFields();
        clearAllErrorLabel();
    }

    @FXML
    void onRegisterAction(ActionEvent event) {
        String usernameText = username.getText();
        String emailText = email.getText();
        String passwordText = password.getText();
        try {
            // Attempt to register the user via the UserService
            RegUser registeredUser = userService.registerUser(usernameText, emailText, passwordText);
            System.out.println(registeredUser);
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Your account has been created! You can now log in.");
            MainApp.setRoot(FXML_LOGIN_VIEW); // Navigate back to the Login screen
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", e.getMessage()); // For username already exists
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "An unexpected error occurred during registration: " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace for debugging
        } finally {
            // Crucial: Clear sensitive password data from memory
            passwordText = null;
            // Clear UI field
            clearAllInputFields();
            clearAllErrorLabel();
        }
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

    private boolean validateUsername() {
        String usernameInput = username.getText();
        if (usernameInput == null || usernameInput.trim().isEmpty()) {
            lblUsernameError.setText("Required!");
            setIcon(lblUsernameError, FontAwesome.TIMES_CIRCLE_O, Color.DARKRED, 16);
            return false;
        } else if (usernameInput.trim().length() < 3 || usernameInput.trim().length() > 15) {
            lblUsernameError.setText("3-15 characters!");
            setIcon(lblUsernameError, FontAwesome.TIMES_CIRCLE_O, Color.DARKRED, 16);
            return false;
        } else {
            lblUsernameError.setText(""); // Clear error
            setIcon(lblUsernameError, FontAwesome.CHECK_CIRCLE, Color.DARKGREEN, 16);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailInput = email.getText();
        if (emailInput == null || emailInput.trim().isEmpty()) {
            lblEmailError.setText("Required!");
            setIcon(lblEmailError, FontAwesome.TIMES_CIRCLE_O, Color.DARKRED, 16);
            return false;
        } else if (!EMAIL_PATTERN.matcher(emailInput.trim()).matches()) {
            lblEmailError.setText("Invalid format - user@domain.com");
            setIcon(lblEmailError, FontAwesome.TIMES_CIRCLE_O, Color.DARKRED, 16);
            return false;
        } else {
            lblEmailError.setText("");
            setIcon(lblEmailError, FontAwesome.CHECK_CIRCLE, Color.DARKGREEN, 16);
            return true;
        }
    }

    private boolean validatePassword() {
        String inputPassword = password.getText();
        if (inputPassword == null || inputPassword.isEmpty()) {
            lblPasswordError.setText("Required!");
            setIcon(lblPasswordError, FontAwesome.TIMES_CIRCLE_O, Color.DARKRED, 16);
            return false;
        } else if (inputPassword.length() < 8) {
            lblPasswordError.setText("Minimum 8 Characters!");
            setIcon(lblPasswordError, FontAwesome.TIMES_CIRCLE_O, Color.DARKRED, 16);
            return false;
        } else if (!PASSWORD_PATTERN.matcher(inputPassword).matches()) {
            lblPasswordError.setText("Contain uppercase, lowercase, number, & special character.");
            setIcon(lblPasswordError, FontAwesome.TIMES_CIRCLE_O, Color.DARKRED, 16);
            return false;
        } else {
            lblPasswordError.setText("");
            setIcon(lblPasswordError, FontAwesome.CHECK_CIRCLE, Color.DARKGREEN, 16);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        clearErrorLabel(lblConfirmPasswordError);
        if (!password.getText().equals(confirmPassword.getText())) {
            lblConfirmPasswordError.setText("Not Matched!");
            setIcon(lblConfirmPasswordError, FontAwesome.TIMES_CIRCLE_O, Color.DARKRED, 16);
            return false;
        } else {
            lblConfirmPasswordError.setText("");
            setIcon(lblConfirmPasswordError, FontAwesome.CHECK_CIRCLE, Color.DARKGREEN, 16);
            return true;
        }
    }

    private void setIcon(Label errorLabel, FontAwesome iconName, Color iconColor, int iconSize) {
        FontIcon icon = new FontIcon();
        icon.setIconLiteral(iconName.getDescription());
        icon.setFill(iconColor);
        icon.setIconSize(iconSize);
        errorLabel.setGraphic(icon);
    }

    private void clearErrorLabel(Label errorLabel) {
        errorLabel.setText(null);
        errorLabel.setGraphic(null);
    }

    private void clearAllInputFields() {
        username.clear();
        email.clear();
        password.clear();
        confirmPassword.clear();
    }

    private void clearAllErrorLabel() {
        clearErrorLabel(lblUsernameError);
        clearErrorLabel(lblEmailError);
        clearErrorLabel(lblPasswordError);
        clearErrorLabel(lblConfirmPasswordError);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
