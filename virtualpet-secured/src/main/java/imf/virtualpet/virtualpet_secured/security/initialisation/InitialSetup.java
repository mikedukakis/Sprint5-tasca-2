package imf.virtualpet.virtualpet_secured.security.initialisation;

import imf.virtualpet.virtualpet_secured.security.dto.UserRegistrationDTO;
import imf.virtualpet.virtualpet_secured.security.entity.Role;
import imf.virtualpet.virtualpet_secured.security.service.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

@Configuration
public class InitialSetup {
    private static final String USER_COLLECTION = "user";
    private static final String VIRTUALPET_COLLECTION = "virtualpet";
    private UserRegistrationDTO userRegistrationDTO;

    @Bean
    public ApplicationRunner setupAdminUser(UserService userService, PasswordEncoder passwordEncoder, ReactiveMongoTemplate reactiveMongoTemplate) {
        return args -> createDatabaseAndCollections(reactiveMongoTemplate)
                .then(
                        userService.findByUsername("admin")
                                .switchIfEmpty(
                                        userService.registerUser(new UserRegistrationDTO("admin", "123456789", Role.ADMIN))
                                )
                ).subscribe();
    }

    private Mono<Void> createDatabaseAndCollections(ReactiveMongoTemplate reactiveMongoTemplate) {
        return reactiveMongoTemplate.createCollection(USER_COLLECTION)
                .onErrorResume(e -> Mono.empty())
                .then(reactiveMongoTemplate.createCollection(VIRTUALPET_COLLECTION)
                        .onErrorResume(e -> Mono.empty()))
                .then();
    }
}
