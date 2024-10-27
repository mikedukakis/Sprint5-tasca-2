package imf.virtualpet.virtualpet_secured.domain.controller;

import imf.virtualpet.virtualpet_secured.domain.entity.VirtualPet;
import imf.virtualpet.virtualpet_secured.domain.service.VirtualPetService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Data
@RestController
public class VirtualPetController {
    private final VirtualPetService virtualPetService;

    @PostMapping("/Create")
//    public Mono<ResponseEntity<VirtualPet>> createPet(VirtualPet pet) {
    public Mono<VirtualPet> createPet(VirtualPet pet) {
        return virtualPetService.createPet(pet);
//                .map(pet -> ResponseEntity.ok());
    }
}
