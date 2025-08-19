package co.tareq.passwordManager.util;

/**
 * Created by Tareq Sefati on 08-Aug-25
 */
public final class AppConstants {

    // A private constructor prevents instantiation
    private AppConstants(){}

    // --- GUI and Layout Constants ---
    public static final String APP_TITLE = "Password Manager";
    public static final double MIN_WINDOW_WIDTH = 800.0;
    public static final double MIN_WINDOW_HEIGHT = 600.0;
    public static final String FXML_LOGIN_VIEW = "views/login.fxml";
    public static final String FXML_URI_SETTING_VIEW = "views/uriSetting.fxml";

    // --- MongoDb Constants ---
    public static final String MONGO_URI_KEY = "mongodb_uri";
    public static final String MONGO_DATABASE = "DbPasswordManager";
}
