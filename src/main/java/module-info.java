module co.tareq.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires java.base;
    requires com.jfoenix;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;


//    opens java.lang.reflect to com.jfoenix;
    opens co.tareq.passwordManager to javafx.fxml;
    opens co.tareq.passwordManager.login to javafx.fxml;
    opens co.tareq.passwordManager.uriSetting to javafx.fxml;
    exports co.tareq.passwordManager;
    exports co.tareq.passwordManager.login;
    exports co.tareq.passwordManager.uriSetting;
}