module co.tareq.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.tareq.passwordManager to javafx.fxml;
    exports co.tareq.passwordManager;
}