package co.tareq.passwordManager.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Created by Tareq Sefati on 21-Aug-25
 */
public class EncryptionUtil {
    private static final int ITERATIONS = 65536; // Number of iterations for PBKDF2
    private static final int KEY_LENGTH = 256;   // Desired key length in bits (for AES-256)
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final String TRANSFORMATION = "AES/GCM/NoPadding"; // AES in GCM mode with no padding
    private static final int GCM_TAG_LENGTH = 16; // 128 bits, used for authentication
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 bytes = 128 bits
        random.nextBytes(salt);
        return salt;
    }

    public static SecretKey deriveKeyFromPassword(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); // Strong KDF
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES"); // Ensure it's an AES key
    }

    public static String encrypt(String plaintext, SecretKey secretKey) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv); // Generate a random IV for each encryption

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv); // Tag length in bits
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());

        // Combine IV and ciphertext for storage/transmission
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String ciphertextWithIvBase64, SecretKey secretKey) throws Exception {
        byte[] combined = Base64.getDecoder().decode(ciphertextWithIvBase64);

        // Extract IV and ciphertext
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);

        byte[] encryptedBytes = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, GCM_IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }
}
