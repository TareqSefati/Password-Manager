package co.tareq.passwordManager.service;

import co.tareq.passwordManager.registration.RegUser;
import co.tareq.passwordManager.util.EncryptionUtil;
import co.tareq.passwordManager.util.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static co.tareq.passwordManager.util.AppConstants.MONGO_USER_COLLECTION;

/**
 * Created by Tareq Sefati on 21-Aug-25
 */
public class UserService {

    private final MongoCollection<RegUser> usersCollection;

    public UserService() {
        this.usersCollection = MongoDBConnection.getInstance().getDatabase().getCollection(MONGO_USER_COLLECTION, RegUser.class);
    }

    public RegUser registerUser(String username, String email, String masterPassword) throws Exception{
        if (getUserByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }
        byte[] salt = EncryptionUtil.generateSalt();
        String hashedPassword = hashPassword(masterPassword.toCharArray(), salt);
        RegUser newRegUser = new RegUser(null, username, email, hashedPassword, Base64.getEncoder().encodeToString(salt));
        usersCollection.insertOne(newRegUser);
        return newRegUser;
    }

    private RegUser getUserByUsername(String username) {
        return usersCollection.find(Filters.eq("username", username)).first();
    }

    // Helper to hash password using PBKDF2
    private String hashPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password, salt, 65536, 256); // Same iterations and key length as EncryptionUtil
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }
}
