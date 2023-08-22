package academy.spring.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	@Bean
	SecurityFilterChain rest(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authz) -> authz
				.requestMatchers(HttpMethod.GET, "/cashcards/**").hasAuthority("SCOPE_cashcard:read")
				.requestMatchers("/cashcards/**").hasAuthority("SCOPE_cashcard:write")
			)
			.oauth2ResourceServer((jwt) -> jwt
				.jwt(Customizer.withDefaults())
			);

		return http.build();
	}
}
