package imf.virtualpet.virtualpet_secured.security.dto;

import lombok.Data;

@Data
public class PasswordUpdateDTO {
    private String id;
    private String username;
    private String newPassword;
}
