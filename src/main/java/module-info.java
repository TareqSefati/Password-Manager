module co.tareq.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires java.base;
    requires com.jfoenix;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires static lombok;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires java.desktop;


//    opens java.lang.reflect to com.jfoenix;
    opens co.tareq.passwordManager to javafx.fxml;
    opens co.tareq.passwordManager.login to javafx.fxml;
    opens co.tareq.passwordManager.registration to javafx.fxml;
    opens co.tareq.passwordManager.uriSetting to javafx.fxml;
    opens co.tareq.passwordManager.service to javafx.fxml;
    opens co.tareq.passwordManager.model to javafx.fxml;
    opens co.tareq.passwordManager.dashboard to javafx.fxml;

    exports co.tareq.passwordManager;
    exports co.tareq.passwordManager.login;
    exports co.tareq.passwordManager.uriSetting;
    exports co.tareq.passwordManager.registration;
    exports co.tareq.passwordManager.model;
    exports co.tareq.passwordManager.dashboard;

}