package imf.virtualpet.virtualpet_secured.security.service;

import imf.virtualpet.virtualpet_secured.security.repository.UserRepository;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Data
@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    List<GrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority(user.getRole().name())
                    );
                    return new User(user.getUsername(), user.getPassword(), authorities);
                });
    }
}
