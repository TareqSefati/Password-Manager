package co.tareq.passwordManager.dashboard;

import co.tareq.passwordManager.model.User;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

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

    private static User currentUser; // Stores the logged-in user

    public static void setCurrentUser(User user) {
        AppDashboardController.currentUser = user;
    }

    @FXML
    public void initialize() {
        constructUsernameButton();
    }

    @FXML
    void onAddEntryAction(ActionEvent event) {

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
}
