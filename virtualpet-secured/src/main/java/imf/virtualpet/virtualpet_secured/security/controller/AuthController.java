package imf.virtualpet.virtualpet_secured.security.controller;

import imf.virtualpet.virtualpet_secured.security.dto.LoginDTO;
import imf.virtualpet.virtualpet_secured.security.dto.PasswordUpdateDTO;
import imf.virtualpet.virtualpet_secured.security.entity.User;
import imf.virtualpet.virtualpet_secured.security.dto.UserDTO;
import imf.virtualpet.virtualpet_secured.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@RestController
@RequestMapping("virtualpet/user")
@Tag(name = "User", description = "Operations for secure user management")
public class AuthController {
    private final UserService userService;

    @Operation(summary = "Create new user", description = "Registers a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created. New user created successfully."),
            @ApiResponse(responseCode = "202", description = "Accepted. User details received for registration, not processed yet."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. Not authorised to create user."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @PostMapping("/new")
    public Mono<User> registerUser(@RequestBody UserDTO userDTO) {
        User user = new User(userDTO.getUsername(), userDTO.getPassword());
        return userService.registerUser(user);
    }

    @Operation(summary = "Find a user", description = "Looks for a user by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok. The user has been found."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. Not authorised to find users."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @GetMapping("/user/find")
    public Mono<User> findByUsername(@RequestParam String userName) {
        return userService.findByUsername(userName);
    }

    @Operation(summary = "User log-in", description = "Logs user in the application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok. The user has been logged in."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. User not authorised to log in."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @GetMapping("/user/login")
    public Mono<User> loginUser(@RequestBody LoginDTO loginDTO) {
        return userService.loginUser(loginDTO.getUsername(), loginDTO.getPassword());
    }

    @Operation(summary = "Delete user", description = "Deletes a user using their ID to locate them.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content. The user has been deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. User not authorised to delete user."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @DeleteMapping("/user/delete/{userId}")
    public Mono<Void> deleteUser(@PathVariable String userId) {
        return userService.deleteUser(userId);
    }

    @Operation(summary = "Find all users", description = "List all users registered.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok. The list has been produced successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request, see body for more details."),
            @ApiResponse(responseCode = "401", description = "Unauthorised. User not authorised to retrieve list."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. Unexpected error in server connection.")
    })
    @GetMapping("user/users")
    public Flux<User> findAllUsers() {
        return userService.findAllUsers();
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
    @Update("user/update-password")
    public Mono<User> updatePassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        return userService.updatePassword(passwordUpdateDTO.getUsername(), passwordUpdateDTO.getNewPassword());
    }

}
