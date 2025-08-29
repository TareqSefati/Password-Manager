package co.tareq.passwordManager.dashboard;

import co.tareq.passwordManager.MainApp;
import co.tareq.passwordManager.model.PasswordEntry;
import co.tareq.passwordManager.model.User;
import co.tareq.passwordManager.service.PasswordEntryService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.List;

import static co.tareq.passwordManager.util.AppConstants.FXML_ENTRY_DETAILS_VIEW;
import static co.tareq.passwordManager.util.AppConstants.FXML_LOGIN_VIEW;

/**
 * Created by Tareq Sefati on 22-Aug-25
 */
public class AppDashboardController {

    @FXML
    private Menu userMenu;

    @FXML
    private MenuItem profileMenuItem;

    @FXML
    private MenuItem changePasswordMenuItem;

    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private JFXButton btnAddEntry;

    @FXML
    private TableView<PasswordEntry> tableView;

    @FXML
    private TableColumn<PasswordEntry, String> colTitle;

    @FXML
    private TableColumn<PasswordEntry, String> colUsername;

    @FXML
    private TableColumn<PasswordEntry, String> colUrl;

    @FXML
    private TableColumn<PasswordEntry, Boolean> colSso;

    @FXML
    private GridPane entryDetailsGrid;

    @FXML
    private Label lblTitleText;

    @FXML
    private Label lblUsernameText;

    @FXML
    private Label lblPasswordText;

    @FXML
    private Label lblUrlText;

    @FXML
    private Label lblUsageFreqText;

    @FXML
    private Label lblCreatedAtText;

    @FXML
    private Label lblUpdatedAtText;

    @FXML
    private ImageView imgQrCode;

    @FXML
    private JFXTextArea lblNotesText;

    private static User currentUser; // Stores the logged-in user
    private final PasswordEntryService passwordEntryService;
    private ObservableList<PasswordEntry> masterPasswordEntries;

    public AppDashboardController() {
        this.passwordEntryService = new PasswordEntryService();
    }

    public static void setCurrentUser(User user) {
        AppDashboardController.currentUser = user;
    }

    @FXML
    public void initialize() {
        constructUsernameButton();
        mapColumnData();
        loadPasswordEntries(); // Initial load of data

        // Add listener for table selection changes to display details
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showEntryDetails(newValue));
    }

    @FXML
    void onAddEntryAction(ActionEvent event) {
        try {
//            MainApp.setRoot(FXML_ENTRY_DETAILS_VIEW);
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(FXML_ENTRY_DETAILS_VIEW));

            // IMPORTANT: Create a ControllerFactory to pass the user to the constructor
            loader.setControllerFactory(param -> {
                if (param.equals(EntryDetailsController.class)) {
                    return new EntryDetailsController(currentUser, this.passwordEntryService);
                }
                try {
                    return param.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    showAlert(Alert.AlertType.ERROR, "Add Entry Error", "An error occurred during opening Add entry window: " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("Failed to create controller: " + param.getName(), e);
                }
            });
            Parent root = loader.load();
            Stage primaryStage = MainApp.getPrimaryStage();
            primaryStage.setResizable(true);
            primaryStage.setScene(new Scene(root));
            primaryStage.sizeToScene(); // Adjust stage size to content
            primaryStage.centerOnScreen(); // Center the stage
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Add Entry Error", "An error occurred during opening Add entry window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void constructUsernameButton() {
        if (currentUser.getUsername().length() > 6) {
            userMenu.setText("Hi, " + currentUser.getUsername().substring(0, 4) + "...");
        }else {
            userMenu.setText("Hi, " + currentUser.getUsername());
        }
        FontIcon icon = new FontIcon();
        icon.setIconLiteral(FontAwesome.ARROW_CIRCLE_O_DOWN.getDescription());
        icon.setIconSize(16);
        userMenu.setGraphic(icon);
    }

    private void loadPasswordEntries() {
        try {
            if (currentUser == null || currentUser.getId().toHexString().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Session Error", "User not logged in. Please re-login.");
                MainApp.setRoot(FXML_LOGIN_VIEW);
                return;
            }
            List<PasswordEntry> entries = passwordEntryService.getAllEntriesForUser(currentUser.getId().toHexString());
            masterPasswordEntries = FXCollections.observableArrayList(entries);
            tableView.setItems(masterPasswordEntries);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load password entries: " + e.getMessage() + ". Please check MongoDB connection and encryption key.");
            e.printStackTrace();
        }
    }

    private void mapColumnData() {
        colTitle.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getTitle());
        });
        colUrl.setCellValueFactory(cellData -> {
            String data = "";
            try {
                data = passwordEntryService.getDecryptedData(cellData.getValue().getUrl());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred during column data mapping: " + e.getMessage());
                e.printStackTrace();
//                throw new RuntimeException(e);
            }
            return new SimpleStringProperty(data);
        });
        colUsername.setCellValueFactory(cellData -> {
            String data = cellData.getValue().getUsername();
            if (data == null || data.isEmpty()){
                return new SimpleStringProperty("");
            }
            String temp = "";
            try {
                temp = passwordEntryService.getDecryptedData(data);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred during column data mapping: " + e.getMessage());
                e.printStackTrace();
//                throw new RuntimeException(e);
            }
            return new SimpleStringProperty(temp);
        });
        colSso.setCellValueFactory(cellData -> {
            return new SimpleBooleanProperty(cellData.getValue().isSsoBasedLogin());
        });
    }

    private void showEntryDetails(PasswordEntry entry) {
        if (entry != null) {
            try {
                lblTitleText.setText(entry.getTitle());
                lblUsernameText.setText(passwordEntryService.getDecryptedData(entry.getUsername()));
                lblPasswordText.setText(passwordEntryService.getDecryptedData(entry.getPassword()));
                lblUrlText.setText(passwordEntryService.getDecryptedData(entry.getUrl()));
                lblUsageFreqText.setText(String.valueOf(entry.getUsageFrequency()));
                lblNotesText.setText(passwordEntryService.getDecryptedData(entry.getNotes()));
                lblCreatedAtText.setText(entry.getCreatedAt().toString());
                lblUpdatedAtText.setText(entry.getUpdatedAt().toString());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Decryption/Display Error", "Could not decrypt or display entry details: " + e.getMessage());
                e.printStackTrace();
                clearEntryDetails(); // Clear details on error
            }
        }else {
            clearEntryDetails(); // Clear details if no entry selected
        }
    }

    private void clearEntryDetails() {
        lblTitleText.setText("");
        lblUsernameText.setText("");
        lblPasswordText.setText("");
        lblUrlText.setText("");
        lblUsageFreqText.setText("");
        lblNotesText.setText("");
        lblCreatedAtText.setText("");
        lblUpdatedAtText.setText("");
        imgQrCode.setImage(null);
//        showPasswordButton.setVisible(false);
//        showPasswordButton.setUserData(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
