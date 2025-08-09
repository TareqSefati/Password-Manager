package co.tareq.passwordManager.util;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static co.tareq.passwordManager.util.AppConstants.MONGO_URI_KEY;

/**
 * Created by Tareq Sefati on 09-Aug-25
 */
public class PreferenceUtil {
    private final Preferences prefs;

    public PreferenceUtil() {
        prefs = Preferences.userNodeForPackage(PreferenceUtil.class);
    }

    public void saveMongoDbUri(String uri) {
        prefs.put(MONGO_URI_KEY, uri);
    }

    public void deleteMongoDbUri() throws BackingStoreException {
        prefs.remove(MONGO_URI_KEY);
        prefs.flush();
    }

    public String getMongoDbUri() {
        return prefs.get(MONGO_URI_KEY, ""); // Return empty string if not found
    }
}
