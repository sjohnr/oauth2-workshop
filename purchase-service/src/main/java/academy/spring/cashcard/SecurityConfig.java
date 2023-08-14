package academy.spring.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers(HttpMethod.GET, "/cashcards/*/purchases").hasAuthority("SCOPE_purchase:read")
				.anyRequest().denyAll()
			)
			.oauth2ResourceServer((jwt) -> jwt
				.jwt(Customizer.withDefaults())
			);

		return http.build();
	}

}
