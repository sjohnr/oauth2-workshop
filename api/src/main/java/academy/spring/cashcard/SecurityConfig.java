package academy.spring.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.TokenExchangeOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authz) -> authz
				.requestMatchers(HttpMethod.GET, "/cashcards/**").hasAuthority("SCOPE_cashcard:read")
				.requestMatchers("/cashcards/**").hasAuthority("SCOPE_cashcard:write")
			)
			.oauth2ResourceServer((jwt) -> jwt
				.jwt(Customizer.withDefaults())
			)
			.oauth2Client(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public OAuth2AuthorizedClientProvider tokenExchange() {
		return new TokenExchangeOAuth2AuthorizedClientProvider();
	}

	@Bean
	public OAuth2AuthorizedClientService authorizedClientService(
			JdbcTemplate jdbcTemplate, ClientRegistrationRepository clientRegistrationRepository) {

		return new JdbcOAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepository);
	}

}
