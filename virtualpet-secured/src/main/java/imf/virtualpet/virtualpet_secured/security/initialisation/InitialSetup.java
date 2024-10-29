package imf.virtualpet.virtualpet_secured.security.initialisation;

import imf.virtualpet.virtualpet_secured.security.dto.UserRegistrationDTO;
import imf.virtualpet.virtualpet_secured.security.entity.Role;
import imf.virtualpet.virtualpet_secured.security.service.UserService;
import lombok.Data;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

@Data
@Configuration
public class InitialSetup {
    private static final String USER_COLLECTION = "user";
    private static final String VIRTUALPET_COLLECTION = "virtualpet";

    @Bean
    public ApplicationRunner setupAdminUser(UserService userService, ReactiveMongoTemplate reactiveMongoTemplate) {
        return args -> createDatabaseAndCollections(reactiveMongoTemplate)
                .then(
                        userService.findByUsername("admin")
                                .switchIfEmpty(
                                        userService.registerUser(new UserRegistrationDTO("admin", "123456789", Role.ROLE_ADMIN))
                                )
                ).subscribe();
    }

    private Mono<Void> createDatabaseAndCollections(ReactiveMongoTemplate reactiveMongoTemplate) {
        return reactiveMongoTemplate.collectionExists(USER_COLLECTION)
                .flatMap(exists -> exists ? Mono.defer(Mono::empty) : reactiveMongoTemplate.createCollection(USER_COLLECTION).then())
                .then(
                        reactiveMongoTemplate.collectionExists(VIRTUALPET_COLLECTION)
                                .flatMap(exists -> exists ? Mono.defer(Mono::empty) : reactiveMongoTemplate.createCollection(VIRTUALPET_COLLECTION).then())
                );
    }
}
