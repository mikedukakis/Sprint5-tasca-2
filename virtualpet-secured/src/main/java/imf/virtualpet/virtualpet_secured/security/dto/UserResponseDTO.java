package imf.virtualpet.virtualpet_secured.security.dto;

import imf.virtualpet.virtualpet_secured.security.entity.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class UserResponseDTO {
    @Id
    private String id;
    @NotBlank
    private String username;
    @NotBlank
    private Role role;

    public UserResponseDTO(String id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;

    }
}
