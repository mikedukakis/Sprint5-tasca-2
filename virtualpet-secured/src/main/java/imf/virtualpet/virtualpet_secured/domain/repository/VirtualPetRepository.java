package imf.virtualpet.virtualpet_secured.domain.repository;

import imf.virtualpet.virtualpet_secured.domain.entity.VirtualPet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface VirtualPetRepository extends ReactiveMongoRepository<VirtualPet, String> {
    Mono<VirtualPet> findByName(String name);
    Mono<VirtualPet> findByNameAndOwnerUsername(String name, String ownerUsername);
    Flux<VirtualPet> findByOwnerUsername(String username);
}
