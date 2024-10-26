package imf.virtualpet.virtualpet_secured.security.service;

import imf.virtualpet.virtualpet_secured.security.dto.PasswordUpdateDTO;
import imf.virtualpet.virtualpet_secured.security.dto.UserRegistrationDTO;
import imf.virtualpet.virtualpet_secured.security.entity.Role;
import imf.virtualpet.virtualpet_secured.security.entity.User;
import imf.virtualpet.virtualpet_secured.security.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserRegistrationDTO userRegistrationDTO;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> registerUser(UserRegistrationDTO userRegistrationDTO) {
        Role role = userRegistrationDTO.getRole() != null ? userRegistrationDTO.getRole() : Role.USER;
        User user = new User(
                userRegistrationDTO.getUsername(),
                passwordEncoder.encode(userRegistrationDTO.getPassword()),
                role
        );

        return userRepository.save(user);
    }

    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<User> loginUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
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
