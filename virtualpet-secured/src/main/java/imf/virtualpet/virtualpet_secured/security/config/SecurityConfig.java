package imf.virtualpet.virtualpet_secured.security.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.security.Key;
import java.time.Instant;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/public/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2Login(oauth2LoginSpec -> oauth2LoginSpec
                        .authenticationSuccessHandler((exchange, authentication) -> Mono.empty())
                        .authenticationFailureHandler((exchange, exception) -> {
                            ServerWebExchange serverWebExchange = exchange.getExchange();
                            return serverWebExchange.getResponse()
                                    .writeWith(Mono.just(serverWebExchange.getResponse()
                                            .bufferFactory()
                                            .wrap(("Authentication failed: " + exception.getMessage()).getBytes())));
                        })
                );
        return http.build();
    }

    private Mono<String> generateToken(Authentication authentication) {
        return Mono.fromCallable(() -> {
            String userId = authentication.getName();
            Instant now = Instant.now();
            Instant expiryDate = now.plusMillis(EXPIRATION_TIME);

            Key secretKey = Keys.hmacShaKeyFor(SecretKey.getSecretKey().getBytes());

            Map<String, Object> claims = Map.of(
                    "sub", userId,
                    "iat", now.getEpochSecond(),
                    "exp", expiryDate.getEpochSecond()
            );

            return Jwts.builder()
                    .claims(claims)
                    .signWith(secretKey)
                    .compact();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}