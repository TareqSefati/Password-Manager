package co.tareq.passwordManager.dashboard;

import co.tareq.passwordManager.MainApp;
import co.tareq.passwordManager.model.PasswordEntry;
import co.tareq.passwordManager.model.User;
import co.tareq.passwordManager.service.PasswordEntryService;
import co.tareq.passwordManager.util.Toast;
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
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

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
    private JFXButton btnEdit;

    @FXML
    private JFXButton btnDelete;

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

    @FXML
    private JFXButton btnUsernameCopy;

    @FXML
    private JFXButton btnPasswordCopy;

    @FXML
    private JFXButton btnUriCopy;

    @FXML
    private JFXButton btnUriGoto;

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

        setIcon(btnUsernameCopy, FontAwesome.COPY, Color.BLUEVIOLET, 12);
        setIcon(btnPasswordCopy, FontAwesome.COPY, Color.BLUEVIOLET, 12);
        setIcon(btnUriCopy, FontAwesome.COPY, Color.BLUEVIOLET, 12);
        setIcon(btnUriGoto, FontAwesome.LINK, Color.BLUEVIOLET, 12);

        // --- 4. Bind the buttons to the binding ---
        btnEdit.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        clearEntryDetails();
    }

    @FXML
    void onAddEntryAction(ActionEvent event) {
        loadEntryDetailsView(null);
    }

    @FXML
    void onDeleteAction(ActionEvent event) {
        PasswordEntry selectedEntry = tableView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to delete this entry: " + selectedEntry.getTitle() + "?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    passwordEntryService.deleteEntry(selectedEntry.getId());
                    masterPasswordEntries.remove(selectedEntry); // Update UI directly for immediate feedback
                    clearEntryDetails(); // Clear details pane
                    Toast.show(event, "❌ 🗑 - Deletion Successful!", 2000);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Deletion Error", "Failed to delete entry: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            Toast.show(event, "Please select entry!", 2000);
        }
    }

    @FXML
    void onEditAction(ActionEvent event) {
        PasswordEntry selectedEntry = tableView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            loadEntryDetailsView(selectedEntry);
        }
    }

    @FXML
    void onUsernameCopyAction(ActionEvent event) {
        copyDataFrom(lblUsernameText.getText(), event);
    }

    @FXML
    void onPasswordCopyAction(ActionEvent event) {
        copyDataFrom(lblPasswordText.getText(), event);
    }
    @FXML
    void onUriCopyAction(ActionEvent event) {
        copyDataFrom(lblUrlText.getText(), event);
    }

    @FXML
    void onUriGotoAction(ActionEvent event) {
        String urlText = lblUrlText.getText();

        // Check if the URL string has a protocol, and add one if it doesn't
        if (!urlText.startsWith("http://") && !urlText.startsWith("https://")) {
            urlText = "http://" + urlText;
        }
        try {
            // Check if the Desktop API is supported on the current platform
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                // Check if the browse action is supported
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    URI uri = new URI(urlText);
                    desktop.browse(uri);
                    Toast.show(event, "Opening URL in Browser 💻🌐", 1000);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Browse action is not supported on this platform.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Desktop is not supported on this platform.");
            }
        } catch (IOException e) {
            // Handle cases where the browser cannot be launched
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open URL. Please check your system configuration.");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // Handle cases where the URL is invalid
            showAlert(Alert.AlertType.ERROR, "Error", "The URL is invalid. Please enter a valid address.");
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            // Handle cases where the required feature is not supported
            showAlert(Alert.AlertType.ERROR, "Error", "This feature is not supported on your operating system.");
            e.printStackTrace();
        }
    }

    private void constructUsernameButton() {
        if (currentUser.getUsername().length() > 6) {
            userMenu.setText("Hi, " + currentUser.getUsername().substring(0, 4) + "...");
        }else {
            userMenu.setText("Hi, " + currentUser.getUsername());
        }

        setMenuIcon(userMenu, FontAwesome.ARROW_CIRCLE_O_DOWN.getDescription());
        setMenuIcon(profileMenuItem, FontAwesome.USER_CIRCLE_O.getDescription());
        setMenuIcon(changePasswordMenuItem, FontAwesome.KEY.getDescription());
        setMenuIcon(logoutMenuItem, FontAwesome.SIGN_OUT.getDescription());
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

    private void setIcon(JFXButton button, FontAwesome iconName, Color iconColor, int iconSize) {
        FontIcon icon = new FontIcon();
        icon.setIconLiteral(iconName.getDescription());
        icon.setFill(iconColor);
        icon.setIconSize(iconSize);
        button.setGraphic(icon);
    }

    private void setMenuIcon(MenuItem menu, String iconName) {
        FontIcon icon = new FontIcon();
        icon.setIconLiteral(iconName);
        icon.setIconSize(16);
        menu.setGraphic(icon);
    }

    private void copyDataFrom(String data, ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(data);
        clipboard.setContent(content);

        // Show toast after copy data
        if (event != null) {
            Toast.show(event, "Text Copied! ✅", 2000);
        }
    }

    private void loadEntryDetailsView(PasswordEntry selectedEntry) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(FXML_ENTRY_DETAILS_VIEW));
            // IMPORTANT: Create a ControllerFactory to pass the user to the constructor
            loader.setControllerFactory(param -> {
                if (param.equals(EntryDetailsController.class)) {
                    return new EntryDetailsController(currentUser, this.passwordEntryService, selectedEntry);
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
