package co.tareq.passwordManager.dashboard;

import co.tareq.passwordManager.MainApp;
import co.tareq.passwordManager.model.User;
import co.tareq.passwordManager.service.PasswordEntryService;
import co.tareq.passwordManager.util.Toast;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.Arrays;

import static co.tareq.passwordManager.util.AppConstants.FXML_APP_DASHBOARD_VIEW;

/**
 * Created by Tareq Sefati on 26-Aug-25
 */
public class EntryDetailsController {

    @FXML
    private JFXTextField titleField;

    @FXML
    private JFXTextField urlField;

    @FXML
    private JFXCheckBox ssoCheckbox;

    @FXML
    private JFXTextField usernameField;

    @FXML
    private JFXTextField passwordField;

    @FXML
    private JFXTextArea notesField;

    @FXML
    private JFXButton btnSaveEntry;

    @FXML
    private JFXButton btnReset;

    @FXML
    private JFXButton btnGoToDashboard;

    private final PasswordEntryService passwordEntryService;
    private final User currentUser;
    private BooleanProperty isTitleValid = new SimpleBooleanProperty(false);

    public EntryDetailsController(User loggedinUser, PasswordEntryService passwordEntryService) {
        this.passwordEntryService = passwordEntryService;
        this.currentUser = loggedinUser;
    }
    @FXML
    public void initialize() {
        titleField.lengthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 0) { // When titleField is empty
                isTitleValid.set(validateTitle());
            }
        });
        btnSaveEntry.disableProperty().bind(isTitleValid.not());
        usernameField.visibleProperty().bind(ssoCheckbox.selectedProperty().not());
        passwordField.visibleProperty().bind(ssoCheckbox.selectedProperty().not());
    }

    @FXML
    void onResetAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void onGoToDashboardAction(ActionEvent event) {
        try {
            MainApp.setRoot(FXML_APP_DASHBOARD_VIEW);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "An error occurred during opening Registration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onSaveEntryAction(ActionEvent event) {
        String title = titleField.getText().trim();
        boolean isSso = ssoCheckbox.isSelected();
        String username = usernameField.getText().trim();
        String password = passwordField.getText(); // Get as String for service layer
        String url = urlField.getText().trim();
        String notes = notesField.getText().trim();

        // --- Basic Validation ---
        if (title.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Title cannot be empty.");
            return; // Stop here, do not proceed or close window
        }
        if (url.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "URL cannot be empty.");
            return; // Stop here, do not proceed or close window
        }

        // --- Specific validation for non-SSO entries ---
        if (!isSso) {
            if (username.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Username cannot be empty for non-SSO entries.");
                return; // Stop here, do not proceed or close window
            }
            if (password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Password cannot be empty for non-SSO entries.");
                return; // Stop here, do not proceed or close window
            }
        }

        // --- Specific validation for SSO entries - must have notes ---
        if (isSso) {
            if (notes.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Notes cannot be empty for SSO entries.");
                return; // Stop here, do not proceed or close window
            }
        }

        // Use a char array for password for security before passing to service
        char[] passwordChars = password.toCharArray();
        try {
//            if ("create".equals(mode)) {
//                passwordEntryService.createEntry(currentUserId, title, isSso, username, password, url, notes);
//                showAlert(Alert.AlertType.INFORMATION, "Success", "Entry created successfully!");
//            } else if ("edit".equals(mode) && currentEntry != null) {
//                passwordEntryService.updateEntry(currentEntry, title, isSso, username, password, url, notes);
//                showAlert(Alert.AlertType.INFORMATION, "Success", "Entry updated successfully!");
//            }
//            this.savedSuccessfully = true; // Mark as saved on success
//            if (currentStage != null) {
//                currentStage.close(); // Close the window only on successful save
//            }
//            System.out.println("Current user in entry detail window: " + currentUser);
            boolean isSucceed = passwordEntryService.createEntry(currentUser.getId().toHexString(), title, isSso, username, password, url, notes);
            if (isSucceed) {
                Toast.show(event, "Entry Insertion Successful! 🚀👍✅", 2000);
                clearFields();
            }else {
                showAlert(Alert.AlertType.ERROR, "Failed!", "Entry Insertion failed!!!");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Operation Failed", "An error occurred: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for debugging
            //this.savedSuccessfully = false; // Mark as not saved on error
            // Do not close the window on error, allow user to see the alert
        } finally {
            // Crucial: Clear sensitive password data from memory after use
            Arrays.fill(passwordChars, ' ');
            passwordField.setText(""); // Clear UI field as well
        }
    }

    private boolean validateTitle() {
        return titleField.getText() != null && titleField.getText().trim().length() >= 3;
    }

    private void clearFields() {
        titleField.clear();
        urlField.clear();
        ssoCheckbox.setSelected(false);
        usernameField.clear();
        passwordField.clear();
        notesField.clear();
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
