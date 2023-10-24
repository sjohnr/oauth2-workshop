package academy.spring.mobileclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.web.server.WebSessionOAuth2ServerAuthorizationRequestRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.csrf.WebSessionServerCsrfTokenRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http
			.authorizeExchange((authorize) -> authorize
				.pathMatchers("/login").permitAll()
				.anyExchange().authenticated()
			)
			.oauth2Login((login) -> login
				.authorizationRequestRepository(new WebSessionOAuth2ServerAuthorizationRequestRepository())
				.authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler())
			)
			.logout((logout) -> logout
				.logoutSuccessHandler(new HttpStatusReturningServerLogoutSuccessHandler(HttpStatus.OK))
			)
			.securityContextRepository(new WebSessionServerSecurityContextRepository())
			.exceptionHandling((exceptions) -> exceptions
				.authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint("/login"))
			)
			.csrf((csrf) -> csrf
				.csrfTokenRepository(new WebSessionServerCsrfTokenRepository())
			);

		return http.build();
	}

}
