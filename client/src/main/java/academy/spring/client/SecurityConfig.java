package academy.spring.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.R2dbcReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http
			.authorizeExchange((authorize) -> authorize
				.anyExchange().authenticated()
			)
			.oauth2Login(Customizer.withDefaults())
			.exceptionHandling((exceptions) -> exceptions
				.authenticationEntryPoint(
					new RedirectServerAuthenticationEntryPoint("/oauth2/authorization/oidc-client"))
			)
			.oauth2Client(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public ReactiveOAuth2AuthorizedClientService authorizedClientService(
			DatabaseClient db, ReactiveClientRegistrationRepository clientRegistrationRepository) {

		return new R2dbcReactiveOAuth2AuthorizedClientService(db, clientRegistrationRepository);
	}

}
