package academy.spring.authserver;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.ui.DefaultResourcesFilter;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.webauthn.management.MapPublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.MapUserCredentialRepository;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

	private final ClientRegistrationRepository clientRegistrationRepository;

	public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
		this.clientRegistrationRepository = clientRegistrationRepository;
	}

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
				.requestMatchers("/login", "/assets/**").permitAll()
				.anyRequest().authenticated()
			)
			.exceptionHandling((exceptions) -> exceptions
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
			)
			.webAuthn((webAuthn) -> webAuthn
				.rpName("WebAuthn Demo")
				.rpId("example.localhost")
				.allowedOrigins("http://localhost:9000")
				.disableDefaultRegistrationPage(true)
			)
			.formLogin(Customizer.withDefaults())
			.addFilter(DefaultResourcesFilter.webauthn())
			.oauth2Login((oauth2) -> oauth2
				.successHandler(authenticationSuccessHandler())
			)
			.logout((logout) -> logout
				.logoutUrl("/logout")
				.logoutSuccessHandler(oidcLogoutSuccessHandler())
			);

		return http.build();
	}

	private AuthenticationSuccessHandler authenticationSuccessHandler() {
		var delegate = new SimpleUrlAuthenticationSuccessHandler("/account");
		var keyGenerator = new Base64StringKeyGenerator(Base64.getEncoder(), 96);
		var userDetailsService = (InMemoryUserDetailsManager) userDetailsService();
		return (request, response, authentication) -> {
			// Workaround (for now) to store an SSO user in the local UserDetailsService
			if (!userDetailsService.userExists(authentication.getName())) {
				var user = User.builder()
					.username(authentication.getName())
					.password(keyGenerator.generateKey())
					.roles("OIDC_USER")
					.build();
				userDetailsService.createUser(user);
			}

			delegate.onAuthenticationSuccess(request, response, authentication);
		};
	}

	@Bean
	public UserDetailsService userDetailsService() {
		User.UserBuilder builder = User.builder();
		UserDetails steve = builder
			.username("steve")
			.password("{noop}password")
			.roles("USER")
			.build();
		UserDetails admin = builder
			.username("admin")
			.password("{noop}admin")
			.roles("ADMIN")
			.build();
		return new InMemoryUserDetailsManager(steve, admin);
	}

	@Bean
	public PublicKeyCredentialUserEntityRepository publicKeyCredentialUserEntityRepository() {
		return new MapPublicKeyCredentialUserEntityRepository();
	}

	@Bean
	public UserCredentialRepository userCredentialRepository() {
		return new MapUserCredentialRepository();
	}

	private LogoutSuccessHandler oidcLogoutSuccessHandler() {
		OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
			new OidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);
		oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/login?logout");

		return oidcLogoutSuccessHandler;
	}

}