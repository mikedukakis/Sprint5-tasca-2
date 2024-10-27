package imf.virtualpet.virtualpet_secured.domain.util;

import imf.virtualpet.virtualpet_secured.domain.dto.VirtualPetCreationDTO;
import imf.virtualpet.virtualpet_secured.domain.dto.VirtualPetResponseDTO;
import imf.virtualpet.virtualpet_secured.domain.entity.VirtualPet;

public class VirtualPetMapper {
    public static VirtualPet fromCreationDTOToEntity(VirtualPetCreationDTO virtualPetCreationDTO) {
        VirtualPet virtualPet = new VirtualPet(
                virtualPetCreationDTO.getName(),
                virtualPetCreationDTO.getPetType(),
                virtualPetCreationDTO.getColour()
        );
        virtualPet.setHungry(virtualPetCreationDTO.isHungry());
        virtualPet.setHappy(virtualPetCreationDTO.isHappy());
        return virtualPet;
    }

    public static VirtualPet fromResponseDTOToEntity(VirtualPetResponseDTO virtualPetResponseDTO) {
        VirtualPet virtualPet = new VirtualPet(
                virtualPetResponseDTO.getName(),
                virtualPetResponseDTO.getPetType(),
                virtualPetResponseDTO.getColour()
        );
        virtualPet.setHungry(virtualPetResponseDTO.isHungry());
        virtualPet.setHappy(virtualPetResponseDTO.isHappy());
        return virtualPet;
    }


}
