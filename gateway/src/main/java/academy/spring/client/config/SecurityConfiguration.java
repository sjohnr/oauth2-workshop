package academy.spring.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

	private final ReactiveClientRegistrationRepository clientRegistrationRepository;

	public SecurityConfiguration(ReactiveClientRegistrationRepository clientRegistrationRepository) {
		this.clientRegistrationRepository = clientRegistrationRepository;
	}

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http
			.authorizeExchange((authorize) -> authorize
				.pathMatchers("/login", "/assets/**").permitAll()
				.anyExchange().authenticated()
			)
			.exceptionHandling((exceptions) -> exceptions
				.authenticationEntryPoint(
					new RedirectServerAuthenticationEntryPoint("/login"))
			)
			.oauth2Login(Customizer.withDefaults())
			.oauth2Client(Customizer.withDefaults())
			.logout((logout) -> logout
				.logoutSuccessHandler(oidcLogoutSuccessHandler())
			);

		return http.build();
	}

	private ServerLogoutSuccessHandler oidcLogoutSuccessHandler() {
		OidcClientInitiatedServerLogoutSuccessHandler oidcLogoutSuccessHandler =
			new OidcClientInitiatedServerLogoutSuccessHandler(this.clientRegistrationRepository);
		oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/login?logout");

		return oidcLogoutSuccessHandler;
	}

}
