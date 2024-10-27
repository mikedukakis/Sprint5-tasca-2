package imf.virtualpet.virtualpet_secured.domain.service;

import imf.virtualpet.virtualpet_secured.domain.dto.VirtualPetCreationDTO;
import imf.virtualpet.virtualpet_secured.domain.entity.VirtualPet;
import imf.virtualpet.virtualpet_secured.domain.repository.VirtualPetRepository;
import imf.virtualpet.virtualpet_secured.domain.util.VirtualPetMapper;
import imf.virtualpet.virtualpet_secured.security.entity.User;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@Service
public class VirtualPetService {
    private final VirtualPetRepository virtualPetRepository;

    public Mono<VirtualPet> createPet(VirtualPetCreationDTO virtualPetCreationDTO) {
        VirtualPet virtualPet = VirtualPetMapper.fromCreationDTOToEntity(virtualPetCreationDTO);
        return virtualPetRepository.save(virtualPet);
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
