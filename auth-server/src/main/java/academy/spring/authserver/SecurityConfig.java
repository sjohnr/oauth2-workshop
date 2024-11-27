package academy.spring.authserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
			throws Exception {

		OAuth2AuthorizationServerConfigurer authorizationServer =
			OAuth2AuthorizationServerConfigurer.authorizationServer()
				.oidc(Customizer.withDefaults());
		http.with(authorizationServer, Customizer.withDefaults());

		http
			.securityMatcher(authorizationServer.getEndpointsMatcher())
			.authorizeHttpRequests((authorize) ->
				authorize.anyRequest().authenticated()
			)
			.exceptionHandling((exceptions) -> exceptions
				.defaultAuthenticationEntryPointFor(
					new LoginUrlAuthenticationEntryPoint("/login"),
					new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
				)
			)
			.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
				.jwt(Customizer.withDefaults())
			);

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
			throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/login").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin((formLogin) -> formLogin
				.loginPage("/login")
			);

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		User.UserBuilder builder = User.withDefaultPasswordEncoder();
		UserDetails steve = builder
			.username("steve")
			.password("password")
			.roles("USER")
			.build();
		UserDetails cora = builder
			.username("cora")
			.password("password")
			.roles("USER")
			.build();
		UserDetails josh = builder
			.username("josh")
			.password("password")
			.roles("USER")
			.build();
		return new InMemoryUserDetailsManager(steve, cora, josh);
	}

}