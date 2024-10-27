package imf.virtualpet.virtualpet_secured.domain.dto;

import imf.virtualpet.virtualpet_secured.domain.entity.PetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VirtualPetCreationDTO {
    @NotBlank
    private String name;
    @NotNull
    private PetType petType;
    @NotBlank
    private String colour;
    @NotNull
    private boolean isHungry;
    @NotNull
    private boolean isHappy;

    public VirtualPetCreationDTO(String name, PetType petType, String colour, boolean isHungry, boolean isHappy) {
        this.name = name;
        this.petType = petType;
        this.colour = colour;
        this.isHungry = isHungry;
        this.isHappy = isHappy;
    }
}
