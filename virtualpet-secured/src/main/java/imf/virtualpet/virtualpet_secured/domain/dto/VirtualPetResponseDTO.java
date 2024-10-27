package imf.virtualpet.virtualpet_secured.domain.dto;

import imf.virtualpet.virtualpet_secured.domain.entity.PetType;
import imf.virtualpet.virtualpet_secured.domain.entity.VirtualPet;
import lombok.Data;

@Data
public class VirtualPetResponseDTO {
    private String id;
    private String name;
    private PetType petType;
    private String colour;
    private Boolean isHungry;
    private Boolean isHappy;

    public VirtualPetResponseDTO(String id, String name, PetType petType, String colour, Boolean isHungry, Boolean isHappy) {
        this.id = id;
        this.name = name;
        this.petType = petType;
        this.colour = colour;
        this.isHungry = isHungry;
        this.isHappy = isHappy;
    }

    public static VirtualPetResponseDTO fromEntity(VirtualPet pet) {
        return new VirtualPetResponseDTO(pet.getId(), pet.getName(), pet.getPetType(), pet.getColour(), pet.isHungry(), pet.isHappy());
    }
}
