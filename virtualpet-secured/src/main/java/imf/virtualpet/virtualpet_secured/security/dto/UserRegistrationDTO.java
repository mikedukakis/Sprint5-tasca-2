package imf.virtualpet.virtualpet_secured.security.dto;

import imf.virtualpet.virtualpet_secured.security.entity.Role;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String username;
    private String password;
    private Role role;

    public UserRegistrationDTO(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
