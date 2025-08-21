package co.tareq.passwordManager.registration;

import javafx.beans.property.StringProperty;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

/**
 * Created by Tareq Sefati on 19-Aug-25
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegUser {

    @BsonId
    private ObjectId id;
    private StringProperty username;
    private StringProperty email;
    private StringProperty password; // Master password hash
    private String salt; // Salt for master password hashing

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
