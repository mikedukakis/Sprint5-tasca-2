package imf.virtualpet.virtualpet_secured.security.dto;

import imf.virtualpet.virtualpet_secured.security.entity.Role;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String username;
    private String password;
    private Role role = Role.ROLE_USER;

    public UserRegistrationDTO() {}

    public UserRegistrationDTO(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
