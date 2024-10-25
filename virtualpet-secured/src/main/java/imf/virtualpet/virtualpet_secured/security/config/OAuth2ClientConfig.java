package imf.virtualpet.virtualpet_secured.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@Configuration
public class OAuth2ClientConfig {

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration registration = ClientRegistration.withRegistrationId("my-client")
                .clientId("Ov23liwDQZtCvfJrirl7")
                .clientSecret("dc1eac0f9340b2ad4c33639db55d609c8258182b")
                .scope("read", "write")
                .authorizationUri("https://github.com/login/oauth/authorize")
                .tokenUri("https://github.com/login/oauth/access_token")
                .userInfoUri("https://api.github.com/user")
                .userNameAttributeName("login")
                .redirectUri("http://localhost:8080/login/oauth2/code/my-client")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();
        return new InMemoryReactiveClientRegistrationRepository(registration);
    }
}