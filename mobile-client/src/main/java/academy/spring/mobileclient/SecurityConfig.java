package academy.spring.mobileclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

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
			.logout((logout) -> logout
				.logoutSuccessHandler(new HttpStatusReturningServerLogoutSuccessHandler())
			)
			.securityContextRepository(new InMemoryServerSecurityContextRepository())
			.exceptionHandling((exceptions) -> exceptions
				.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
			)
			.csrf(ServerHttpSecurity.CsrfSpec::disable);

		return http.build();
	}

}
