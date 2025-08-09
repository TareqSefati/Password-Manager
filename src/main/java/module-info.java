module co.tareq.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;


    opens co.tareq.passwordManager to javafx.fxml;
    exports co.tareq.passwordManager;
}