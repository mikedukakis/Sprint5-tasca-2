package imf.virtualpet.virtualpet_secured.security.service;

import imf.virtualpet.virtualpet_secured.security.dto.PasswordUpdateDTO;
import imf.virtualpet.virtualpet_secured.security.dto.UserRegistrationDTO;
import imf.virtualpet.virtualpet_secured.security.entity.Role;
import imf.virtualpet.virtualpet_secured.security.entity.User;
import imf.virtualpet.virtualpet_secured.security.jwt.JwtUtil;
import imf.virtualpet.virtualpet_secured.security.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Mono<User> registerUser(UserRegistrationDTO registrationDTO) {
        logger.info("Attempting to register user: {}", registrationDTO.getUsername());

        return userRepository.findByUsername(registrationDTO.getUsername())
                .flatMap((User existingUser) -> {
                    logger.info("User already exists: {}", existingUser.getUsername());
                    return Mono.<User>error(new RuntimeException("User already exists"));
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            logger.info("Saving new user: {}", registrationDTO.getUsername());
                            return userRepository.save(new User(
                                    registrationDTO.getUsername(),
                                    passwordEncoder.encode(registrationDTO.getPassword()),
                                    Role.ROLE_USER
                            )).map((User savedUser) -> { // Explicitly set savedUser type to User
                                logger.info("User saved: {}", savedUser.getUsername());
                                return savedUser; // Explicitly return savedUser as User
                            });
                        })
                )
                .cast(User.class)
                .doOnError(error -> logger.error("Error saving user: {}", error.getMessage()));
    }


    public Mono<String> loginUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> jwtUtil.generateToken(user.getUsername()));
    }

    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<Void> deleteUser(String userId) {
        return userRepository.deleteById(userId);
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> updatePassword(PasswordUpdateDTO passwordUpdateDTO) {
        return userRepository.findByUsername(passwordUpdateDTO.getUsername())
                .flatMap(user -> {
                    user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
                    return userRepository.save(user);
                });
    }
}
