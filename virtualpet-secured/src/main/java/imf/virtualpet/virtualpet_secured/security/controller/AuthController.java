package imf.virtualpet.virtualpet_secured.security.controller;

import imf.virtualpet.virtualpet_secured.security.dto.*;
import imf.virtualpet.virtualpet_secured.security.entity.Role;
import imf.virtualpet.virtualpet_secured.security.service.UserService;
import lombok.Data;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@RestController
@RequestMapping("/virtualpet")
public class AuthController {
    private final UserService userService;

    @PostMapping("/new")
    public Mono<ResponseEntity<UserResponseDTO>> registerUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        return userService.registerUser(userRegistrationDTO)
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user.getId(), user.getUsername(), user.getRole())))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new UserResponseDTO("Error", "Registration failed", Role.USER))));
    }

    @GetMapping("/user/find")
    public Mono<ResponseEntity<UserResponseDTO>> findByUsername(@RequestParam String userName) {
        return userService.findByUsername(userName)
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user.getId(), user.getUsername(), user.getRole())))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new UserResponseDTO("Error", "Database error occurred", Role.USER))));
    }

    @GetMapping("/user/login")
    public Mono<ResponseEntity<UserResponseDTO>> loginUser(@RequestBody LoginDTO loginDTO) {
        return userService.loginUser(loginDTO.getUsername(), loginDTO.getPassword())
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user.getId(), user.getUsername(), user.getRole())))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new UserResponseDTO("Error", "Database error occurred", Role.USER))));
    }

    @DeleteMapping("/user/delete/{userId}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String userId) {
        return userService.deleteUser(userId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @GetMapping("user/users")
    public Flux<ResponseEntity<UserResponseDTO>> findAllUsers() {
        return userService.findAllUsers()
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user.getId(), user.getUsername(), user.getRole())))
                .onErrorResume(error -> Flux.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new UserResponseDTO("Error", "Database error occurred", Role.USER))));
    }

    @Update("user/update-password")
    public Mono<ResponseEntity<UserResponseDTO>> updatePassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        return userService.updatePassword(passwordUpdateDTO)
                .map(user -> ResponseEntity.ok(new UserResponseDTO(user.getId(), user.getUsername(), user.getRole())))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new UserResponseDTO("Error", "Password update failed", Role.USER))));
    }

}
