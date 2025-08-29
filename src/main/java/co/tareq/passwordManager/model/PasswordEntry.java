package co.tareq.passwordManager.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

/**
 * Created by Tareq Sefati on 26-Aug-25
 */
@Getter
@Setter
@ToString
public class PasswordEntry {

    @BsonId
    private ObjectId id;
    private String userId; // Link to the user who owns this entry
    private String title;
    private boolean ssoBasedLogin;
    // Encrypted fields - will be stored as Base64 encoded strings
    private String username;
    private String password;
    private String url;
    private String notes;
//    private String encryptionSalt; // Salt for entry-specific encryption
//    private String encryptionIV; // IV for entry-specific encryption

    private int usageFrequency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PasswordEntry() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.usageFrequency = 0;
    }
}
