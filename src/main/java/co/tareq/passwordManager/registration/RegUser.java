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
@Getter
@Setter
@ToString
public class RegUser {

    @BsonId
    private ObjectId id;
    private String username;
    private String email;
    private String password; // Master password hash
    private String salt; // Salt for master password hashing
}
