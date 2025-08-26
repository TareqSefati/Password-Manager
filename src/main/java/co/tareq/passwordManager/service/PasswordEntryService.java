package co.tareq.passwordManager.service;

import javax.crypto.SecretKey;

/**
 * Created by Tareq Sefati on 25-Aug-25
 */
public class PasswordEntryService {

    private static SecretKey masterEncryptionKey; // Derived from master password

    public void setMasterEncryptionKey(SecretKey key) {
        System.out.println("Master encryption key is set.");
        masterEncryptionKey = key;
    }
}
