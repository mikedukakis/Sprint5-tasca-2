package imf.virtualpet.virtualpet_secured.security.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document
public class User {
    @Id
    private String id;
    @Field("User")
    private String username;
    @Field("Password")
    private String password;
    @Field("Role")
    private Role role;

    public User() {}

    public User(String userName, String password) {
        this.username = userName;
        this.password = password;
        this.role = Role.USER;
    }

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
