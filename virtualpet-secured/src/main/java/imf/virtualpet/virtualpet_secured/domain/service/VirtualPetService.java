package imf.virtualpet.virtualpet_secured.domain.service;

import imf.virtualpet.virtualpet_secured.domain.entity.VirtualPet;
import imf.virtualpet.virtualpet_secured.domain.repository.VirtualPetRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@Service
public class VirtualPetService {
    private final VirtualPetRepository virtualPetRepository;

    public Mono<VirtualPet> createPet(VirtualPet pet) {
        return virtualPetRepository.save(pet);
    }

    public Mono<VirtualPet> findPet(String petId) {
        return virtualPetRepository.findById(petId);
    }

    public Flux<VirtualPet> findAllPets() {
        return virtualPetRepository.findAll();
    }

//    public Mono<VirtualPet> updatePet() {
//
//    }

    public Mono<Void> deletePet(String petId) {
        return virtualPetRepository.deleteById(petId);
    }

}
