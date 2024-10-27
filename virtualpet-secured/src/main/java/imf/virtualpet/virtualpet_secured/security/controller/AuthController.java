package imf.virtualpet.virtualpet_secured.security.controller;

import imf.virtualpet.virtualpet_secured.security.dto.*;
import imf.virtualpet.virtualpet_secured.security.entity.Role;
import imf.virtualpet.virtualpet_secured.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@RestController
@RequestMapping("virtualpet/user")
@Tag(name = "User", description = "Operations for secure user management")
public class AuthController {
    private final UserService userService;

    @Operation(summary = "Creates new user", description = "Registers a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created. New user created successfully."),
            @ApiResponse(responseCode = "202", description = "Accepted. User details received for registration, not processed yet."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. Not authorised to create user."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @PostMapping("/new")
    public Mono<ResponseEntity<UserResponseDTO>> registerUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        return userService.registerUser(userRegistrationDTO)
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user.getId(), user.getUsername(), user.getRole())))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new UserResponseDTO("Error", "Registration failed", Role.USER))));
    }

    @Operation(summary = "Find a user", description = "Looks for a user by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok. The user has been found and is returned."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. Not authorised to find users."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @GetMapping("/find/{username}")
    public Mono<ResponseEntity<UserResponseDTO>> findByUsername(@RequestParam String userName) {
        return userService.findByUsername(userName)
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user.getId(), user.getUsername(), user.getRole())))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new UserResponseDTO("Error", "Database error occurred", Role.USER))));
    }

    @Operation(summary = "User login", description = "Logs user in the application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok. The user has been logged in."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. User not authorised to log in."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @PostMapping("/login")
    public Mono<ResponseEntity<UserResponseDTO>> loginUser(@RequestBody LoginDTO loginDTO) {
        return userService.loginUser(loginDTO.getUsername(), loginDTO.getPassword())
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user.getId(), user.getUsername(), user.getRole())))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new UserResponseDTO("Error", "Database error occurred", Role.USER))));
    }

    @Operation(summary = "Delete user", description = "Deletes a user using their ID to locate them.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content. The user has been deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. User not authorised to delete user."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @DeleteMapping("/delete/{userId}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String userId) {
        return userService.deleteUser(userId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @Operation(summary = "Find all users", description = "List all users registered.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok. The list has been produced successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. User not authorised to retrieve list."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @GetMapping("/users")
    public Flux<ResponseEntity<UserResponseDTO>> findAllUsers() {
        return userService.findAllUsers()
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user.getId(), user.getUsername(), user.getRole())))
                .onErrorResume(error -> Flux.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new UserResponseDTO("Error", "Database error occurred", Role.USER))));
    }

    @Operation(summary = "Update password", description = "Changes the password of a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok. The password has been successfully changed."),
            @ApiResponse(responseCode = "202", description = "Accepted. User details received for registration, not processed yet."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. Not authorised to update the password."),
            @ApiResponse(responseCode = "409", description = "Conflict. The update could not be completed (possibly another client has modified the password)"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @PutMapping("user/update-password")
    public Mono<ResponseEntity<UserResponseDTO>> updatePassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        return userService.updatePassword(passwordUpdateDTO)
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user.getId(), user.getUsername(), user.getRole())))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new UserResponseDTO("Error", "Password update failed", Role.USER))));
    }

}
