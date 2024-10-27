package imf.virtualpet.virtualpet_secured.domain.repository;

import imf.virtualpet.virtualpet_secured.domain.entity.VirtualPet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VirtualPetRepository extends ReactiveMongoRepository<VirtualPet, String> {}
