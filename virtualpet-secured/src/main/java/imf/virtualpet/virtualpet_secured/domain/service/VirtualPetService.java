package imf.virtualpet.virtualpet_secured.domain.service;

import imf.virtualpet.virtualpet_secured.domain.dto.VirtualPetCreationDTO;
import imf.virtualpet.virtualpet_secured.domain.entity.VirtualPet;
import imf.virtualpet.virtualpet_secured.domain.repository.VirtualPetRepository;
import imf.virtualpet.virtualpet_secured.domain.util.VirtualPetMapper;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@Service
public class VirtualPetService {
    private final VirtualPetRepository virtualPetRepository;

    public Flux<VirtualPet> findPetsByUsername(String username) {
        return virtualPetRepository.findByOwnerUsername(username);
    }

    public Mono<VirtualPet> findPetByNameAndUsername(String name, String username) {
        return virtualPetRepository.findByNameAndOwnerUsername(name, username);
    }

    public Mono<VirtualPet> createPet(VirtualPetCreationDTO virtualPetCreationDTO, String ownerUsername) {
        VirtualPet virtualPet = VirtualPetMapper.fromCreationDTOToEntity(virtualPetCreationDTO);
        virtualPet.setOwnerUsername(ownerUsername);
        return virtualPetRepository.save(virtualPet);
    }

    public Mono<Void> deletePetIfOwner(String petId, String username) {
        return virtualPetRepository.findById(petId)
                .filter(virtualPet -> virtualPet.getOwnerUsername().equals(username))
                .flatMap(virtualPetRepository::delete);
    }

    public Mono<VirtualPet> findPet(String name) {
        return virtualPetRepository.findByName(name);
    }

    public Flux<VirtualPet> findAllPets() {
        return virtualPetRepository.findAll();
    }

    public Mono<Void> deletePet(String petId) {
        return virtualPetRepository.deleteById(petId);
    }

}
