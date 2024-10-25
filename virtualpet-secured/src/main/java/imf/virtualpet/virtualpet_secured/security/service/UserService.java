package imf.virtualpet.virtualpet_secured.security.service;

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
    private final PasswordEncoder passwordEncoder;

    public Mono<User> registerUser(User user) {
        if (user.getId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public Mono<User> findByUsername(String userName) {
        return userRepository.findByUsername(userName);
    }

    public Mono<User> loginUser(String userName, String password) {
        return userRepository.findByUsername(userName)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    public Mono<Void> deleteUser(String userId) {
        return userRepository.deleteById(userId);
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> updatePassword(String username, String newPassword) {
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    return userRepository.save(user);
                });
    }
}
