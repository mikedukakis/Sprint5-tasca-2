package imf.virtualpet.virtualpet_secured.security.controller;

import imf.virtualpet.virtualpet_secured.security.dto.*;
import imf.virtualpet.virtualpet_secured.security.service.UserService;
import lombok.Data;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@RestController
@RequestMapping("/virtualpet")
public class AuthController {
    private final UserService userService;

    @PostMapping("/new")
    public Mono<UserResponseDTO> registerUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        return userService.registerUser(userRegistrationDTO)
                .map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getRole()));
    }

    @GetMapping("/user/find")
    public Mono<UserResponseDTO> findByUsername(@RequestParam String userName) {
        return userService.findByUsername(userName)
                .map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getRole()));
    }

    @GetMapping("/user/login")
    public Mono<UserResponseDTO> loginUser(@RequestBody LoginDTO loginDTO) {
        return userService.loginUser(loginDTO.getUsername(), loginDTO.getPassword())
                .map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getRole()));
    }

    @DeleteMapping("/user/delete/{userId}")
    public Mono<Void> deleteUser(@PathVariable String userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping("user/users")
    public Flux<UserResponseDTO> findAllUsers() {
        return userService.findAllUsers()
                .map(user -> new UserResponseDTO(user.getId(),user.getUsername(), user.getRole()));
    }

    @Update("user/update-password")
    public Mono<UserResponseDTO> updatePassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        return userService.updatePassword(passwordUpdateDTO)
                .map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getRole()));
    }

}
