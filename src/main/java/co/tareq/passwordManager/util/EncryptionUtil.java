package co.tareq.passwordManager.util;

import java.security.SecureRandom;

/**
 * Created by Tareq Sefati on 21-Aug-25
 */
public class EncryptionUtil {
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 bytes = 128 bits
        random.nextBytes(salt);
        return salt;
    }
}
