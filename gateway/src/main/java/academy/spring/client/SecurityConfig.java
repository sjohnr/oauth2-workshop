package academy.spring.client;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayReactiveOAuth2AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;

@Configuration
@EnableWebFluxSecurity
@EnableAutoConfiguration(exclude = GatewayReactiveOAuth2AutoConfiguration.class)
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

}
