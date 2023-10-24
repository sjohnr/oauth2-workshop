package academy.spring.mobileclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
			ReactiveClientRegistrationRepository clientRegistrationRepository) {

		http
			.authorizeExchange((authorize) -> authorize
				.pathMatchers("/callback", "/login").permitAll()
				.anyExchange().authenticated()
			)
			.oauth2Login((login) -> login
				.authorizationRequestRepository(new InMemoryServerAuthorizationRequestRepository())
				.authenticationSuccessHandler(
					new SecurityContextIdRedirectServerAuthenticationSuccessHandler("/callback"))
			)
			.logout((logout) -> {
				OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler =
					new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
				logoutSuccessHandler.setPostLogoutRedirectUri("http://127.0.0.1:8080/");

				logout.logoutSuccessHandler(logoutSuccessHandler);
			})
			.securityContextRepository(new InMemoryServerSecurityContextRepository())
			.exceptionHandling((exceptions) -> exceptions
				.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
			)
			.csrf(ServerHttpSecurity.CsrfSpec::disable);

		return http.build();
	}

}
