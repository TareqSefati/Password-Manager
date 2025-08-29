package co.tareq.passwordManager.service;

import co.tareq.passwordManager.model.PasswordEntry;
import co.tareq.passwordManager.util.EncryptionUtil;
import co.tareq.passwordManager.util.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;

import static co.tareq.passwordManager.util.AppConstants.MONGO_PASSWORD_ENTRY_COLLECTION;

/**
 * Created by Tareq Sefati on 25-Aug-25
 */
public class PasswordEntryService {

    private static SecretKey masterEncryptionKey; // Derived from master password

    private MongoCollection<PasswordEntry> passwordEntryCollection;
    private MongoCollection<PasswordEntry> getPasswordEntryCollection() {
        if (passwordEntryCollection != null){
            return passwordEntryCollection;
        }
        passwordEntryCollection = MongoDBConnection.getInstance().getDatabase().getCollection(MONGO_PASSWORD_ENTRY_COLLECTION, PasswordEntry.class);
        return passwordEntryCollection;
    }

    public void setMasterEncryptionKey(SecretKey key) {
        System.out.println("Master encryption key is set.");
        masterEncryptionKey = key;
    }

    public boolean createEntry(String userId, String title, boolean ssoBasedLogin, String username, String password, String url, String notes) throws Exception {
        checkMasterKey();

        PasswordEntry entry = new PasswordEntry();
        entry.setUserId(userId);
        entry.setTitle(title);
        entry.setSsoBasedLogin(ssoBasedLogin);
        entry.setUrl(checkAndEncrypt(url));

        entry.setUsername(checkAndEncrypt(username));
        entry.setPassword(checkAndEncrypt(password));
        entry.setNotes(checkAndEncrypt(notes));

        getPasswordEntryCollection().insertOne(entry);
//        System.out.println("Created password entry: " + entry);
        return !entry.getId().toHexString().isEmpty();
    }

    public List<PasswordEntry> getAllEntriesForUser(String userId) {
        checkMasterKey();
        List<PasswordEntry> passwordEntryList = new ArrayList<>();
        getPasswordEntryCollection().find(Filters.eq("userId", userId)).into(passwordEntryList);
        return passwordEntryList;
    }

    public String getDecryptedData(String encryptedData) throws Exception {
        if (encryptedData != null && !encryptedData.isEmpty()){
            return EncryptionUtil.decrypt(encryptedData, masterEncryptionKey);
        }
        return "";
    }

    private void checkMasterKey() {
        if (masterEncryptionKey == null) {
            throw new IllegalStateException("Master encryption key is not set. Please log in.");
        }
    }

    private String checkAndEncrypt(String sensitiveData) throws Exception {
        if (sensitiveData != null && !sensitiveData.isEmpty()) {
            return EncryptionUtil.encrypt(sensitiveData, masterEncryptionKey);
        }
        return "";
    }
}
