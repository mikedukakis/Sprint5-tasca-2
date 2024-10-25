package imf.virtualpet.virtualpet_secured.security.controller;

import imf.virtualpet.virtualpet_secured.security.dto.LoginDTO;
import imf.virtualpet.virtualpet_secured.security.dto.PasswordUpdateDTO;
import imf.virtualpet.virtualpet_secured.security.entity.User;
import imf.virtualpet.virtualpet_secured.security.dto.UserDTO;
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
    public Mono<User> registerUser(@RequestBody UserDTO userDTO) {
        User user = new User(userDTO.getUsername(), userDTO.getPassword());
        return userService.registerUser(user);
    }

    @GetMapping("/user/find")
    public Mono<User> findByUsername(@RequestParam String userName) {
        return userService.findByUsername(userName);
    }

    @GetMapping("/user/login")
    public Mono<User> loginUser(@RequestBody LoginDTO loginDTO) {
        return userService.loginUser(loginDTO.getUsername(), loginDTO.getPassword());
    }

    @DeleteMapping("/user/delete/{userId}")
    public Mono<Void> deleteUser(@PathVariable String userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping("user/users")
    public Flux<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @Update("user/update-password")
    public Mono<User> updatePassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        return userService.updatePassword(passwordUpdateDTO.getUsername(), passwordUpdateDTO.getNewPassword());
    }

}
