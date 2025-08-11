module co.tareq.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires java.base;
    requires com.jfoenix;


//    opens java.lang.reflect to com.jfoenix;
    opens co.tareq.passwordManager to javafx.fxml;
    exports co.tareq.passwordManager;
    exports co.tareq.passwordManager.login;
}