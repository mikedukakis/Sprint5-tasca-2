package imf.virtualpet.virtualpet_secured.domain.controller;

import imf.virtualpet.virtualpet_secured.domain.dto.VirtualPetCreationDTO;
import imf.virtualpet.virtualpet_secured.domain.dto.VirtualPetResponseDTO;
import imf.virtualpet.virtualpet_secured.domain.entity.VirtualPet;
import imf.virtualpet.virtualpet_secured.domain.service.VirtualPetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Data
@RestController
@RequestMapping("virtualpet/pet")
public class VirtualPetController {
    private final VirtualPetService virtualPetService;

    @Operation(summary = "Get all authenticated user pets", description = "Retrieve all pets created by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success. User's pet list retrieved successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User not authorized to access pets."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error.")
    })
    @GetMapping("/mypets")
    public Flux<VirtualPet> getUserPets(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .map(Principal::getName)
                .flatMapMany(virtualPetService::findPetsByUsername);
    }

    @Operation(summary = "Create a new pet for user", description = "Allows authenticated users to create a new pet.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created. New pet created successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid pet data provided."),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User not authorized to create pets."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error.")
    })
    @PostMapping("/new")
    public Mono<ResponseEntity<VirtualPetResponseDTO>> createPet(ServerWebExchange exchange,
                                                                 @RequestBody VirtualPetCreationDTO virtualPetCreationDTO) {
        return exchange.getPrincipal()
                .map(Principal::getName)
                .flatMap(username -> virtualPetService.createPet(virtualPetCreationDTO, username))
                .map(pet -> ResponseEntity.ok(new VirtualPetResponseDTO(
                        pet.getId(),
                        pet.getName(),
                        pet.getOwnerUsername(),
                        pet.getPetType(),
                        pet.getColour(),
                        pet.isHungry(),
                        pet.isHappy()
                )))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new VirtualPetResponseDTO(
                                        null,
                                        "Error",
                                        "Error",
                                        null,
                                        "Unknown",
                                        null,
                                        null
                                ))
                ));
    }

    @Operation(summary = "Find a user's pet by name", description = "Retrieve details of a specific pet owned by the authenticated user by pet name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success. Pet details retrieved."),
            @ApiResponse(responseCode = "404", description = "Not found. No pet found with the specified name."),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User not authorized to retrieve pet."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error.")
    })
    @GetMapping("/find/{name}")
    public Mono<ResponseEntity<VirtualPetResponseDTO>> findByName(ServerWebExchange exchange, @PathVariable String name) {
        return exchange.getPrincipal()
                .map(Principal::getName)
                .flatMap(username -> virtualPetService.findPetByNameAndUsername(name, username))
                .map(pet -> ResponseEntity.ok(new VirtualPetResponseDTO(
                        pet.getId(),
                        pet.getName(),
                        pet.getOwnerUsername(),
                        pet.getPetType(),
                        pet.getColour(),
                        pet.isHungry(),
                        pet.isHappy()
                )))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new VirtualPetResponseDTO(
                                        null,
                                        "Error",
                                        "Error",
                                        null,
                                        "Unknown",
                                        null,
                                        null
                                ))
                ));
    }

    @Operation(summary = "Delete a pet", description = "Deletes a pet using their ID, if the authenticated user is the owner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content. The pet has been deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Not found. No pet found with the specified ID."),
            @ApiResponse(responseCode = "401", description = "Unauthorized. User not authorized to delete pet."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error.")
    })
    @DeleteMapping("/delete/{petId}")
    public Mono<ResponseEntity<Void>> deletePet(ServerWebExchange exchange, @PathVariable String petId) {
        return exchange.getPrincipal()
                .map(Principal::getName)
                .flatMap(username -> virtualPetService.deletePetIfOwner(petId, username))
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }
}
