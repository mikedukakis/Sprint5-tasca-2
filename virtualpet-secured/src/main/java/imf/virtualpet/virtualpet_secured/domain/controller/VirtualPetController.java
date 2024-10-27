package imf.virtualpet.virtualpet_secured.domain.controller;

import imf.virtualpet.virtualpet_secured.domain.dto.VirtualPetCreationDTO;
import imf.virtualpet.virtualpet_secured.domain.dto.VirtualPetResponseDTO;
import imf.virtualpet.virtualpet_secured.domain.service.VirtualPetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@RestController
@RequestMapping("virtualpet/pet")
public class VirtualPetController {
    private final VirtualPetService virtualPetService;

    @Operation(summary = "Creates new pet", description = "Creates a new pet.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created. New pet created successfully."),
            @ApiResponse(responseCode = "202", description = "Accepted. Pet details received for registration, not processed yet."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. Not authorised to create user."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @PostMapping("/new")
    public Mono<ResponseEntity<VirtualPetResponseDTO>> createPet(@RequestBody VirtualPetCreationDTO virtualPetCreationDTO) {
        return virtualPetService.createPet(virtualPetCreationDTO)
                .map(pet -> ResponseEntity.ok(new VirtualPetResponseDTO(
                        pet.getId(),
                        pet.getName(),
                        pet.getPetType(),
                        pet.getColour(),
                        pet.isHungry(),
                        pet.isHappy())))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new VirtualPetResponseDTO(
                                        null,
                                        "Error",
                                        null,
                                        "Unknown",
                                        null,
                                        null
                                ))
                ));
    }

    @Operation(summary = "Find a pet", description = "Looks for a pet by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok. The pet has been found."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. Not authorised to find users."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @GetMapping("/find/{name}")
    public Mono<ResponseEntity<VirtualPetResponseDTO>> findByName(@RequestParam String name) {
        return virtualPetService.findPet(name)
                .map(pet -> ResponseEntity.ok(new VirtualPetResponseDTO(
                        pet.getId(),
                        pet.getName(),
                        pet.getPetType(),
                        pet.getColour(),
                        pet.isHungry(),
                        pet.isHappy())))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new VirtualPetResponseDTO(
                                        null,
                                        "Error",
                                        null,
                                        "Unknown",
                                        null,
                                        null
                                ))
                ));
    }

    @Operation(summary = "Find all pets", description = "List all pets created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok. The list has been produced successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. User not authorised to retrieve list."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @GetMapping("/pets")
    public Flux<ResponseEntity<VirtualPetResponseDTO>> findAllPets() {
        return virtualPetService.findAllPets()
                .map(pet -> ResponseEntity.ok(new VirtualPetResponseDTO(
                        pet.getId(),
                        pet.getName(),
                        pet.getPetType(),
                        pet.getColour(),
                        pet.isHungry(),
                        pet.isHappy())))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(error -> Flux.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new VirtualPetResponseDTO(
                                        null,
                                        "Error",
                                        null,
                                        "Unknown",
                                        null,
                                        null
                                ))
                ));
    }

    @Operation(summary = "Delete pet", description = "Deletes a pet using their ID to locate them.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content. The pet has been deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. User not authorised to delete pet."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @DeleteMapping("/delete/{ºpetId}")
    public Mono<ResponseEntity<Void>> deletePet(@PathVariable String petId) {
        return virtualPetService.deletePet(petId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }
}
