package imf.virtualpet.virtualpet_secured.security.config;

import imf.virtualpet.virtualpet_secured.security.jwt.JwtUtil;
import imf.virtualpet.virtualpet_secured.security.service.CustomUserDetailsService;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.net.URI;

@Data
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/virtualpet/user/signup", "/virtualpet/user/login").permitAll()
                        .pathMatchers("/login.html", "/signup.html", "/styles.css", "/public/**").permitAll()
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler(new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN))
                )
                .addFilterBefore(redirectFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public WebFilter redirectFilter() {
        return (exchange, chain) -> {
            if (exchange.getRequest().getMethod() == HttpMethod.GET) {
                String path = exchange.getRequest().getURI().getPath();

                return exchange.getPrincipal()
                        .flatMap(principal -> chain.filter(exchange)) // Authenticated, proceed as usual
                        .switchIfEmpty( // Unauthenticated, check for specific paths and redirect
                                Mono.defer(() -> {
                                    if (path.equals("/virtualpet/user/login")) {
                                        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                                        exchange.getResponse().getHeaders().setLocation(URI.create("/login.html"));
                                        return exchange.getResponse().setComplete();
                                    } else if (path.equals("/virtualpet/user/signup")) {
                                        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                                        exchange.getResponse().getHeaders().setLocation(URI.create("/signup.html"));
                                        return exchange.getResponse().setComplete();
                                    }
                                    return chain.filter(exchange);
                                })
                        );
            }
            return chain.filter(exchange);
        };
    }

    private boolean isAuthenticated(ServerWebExchange exchange) {
        return exchange.getPrincipal().map(principal -> principal instanceof UsernamePasswordAuthenticationToken).blockOptional().orElse(false);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private WebFilter jwtAuthenticationFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();

            if (path.equals("/login.html") || path.equals("/signup.html") || path.equals("/styles.css") || path.startsWith("/public/")) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);

                return userDetailsService.findByUsername(username)
                        .flatMap(userDetails -> {
                            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                                SecurityContext context = new SecurityContextImpl(auth);
                                return chain.filter(exchange)
                                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                            } else {
                                return Mono.error(new RuntimeException("Invalid Token"));
                            }
                        });
            }
            return chain.filter(exchange);
        };
    }
}