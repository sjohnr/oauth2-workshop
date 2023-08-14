package academy.spring.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.ui.DefaultResourcesFilter;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private final ClientRegistrationRepository clientRegistrationRepository;

	public SecurityConfiguration(ClientRegistrationRepository clientRegistrationRepository) {
		this.clientRegistrationRepository = clientRegistrationRepository;
	}

	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
			throws Exception {

		var springAuthorizationServer = OAuth2AuthorizationServerConfigurer.authorizationServer();
		http
			.securityMatcher(springAuthorizationServer.getEndpointsMatcher())
			.authorizeHttpRequests((authorize) ->
				authorize.anyRequest().authenticated()
			)
			.exceptionHandling((exceptions) -> exceptions
				.defaultAuthenticationEntryPointFor(
					new LoginUrlAuthenticationEntryPoint("/login"),
					new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
				)
			)
			.oauth2ResourceServer((oauth2) -> oauth2
				.jwt(Customizer.withDefaults())
			)
			.with(springAuthorizationServer, (authorizationServer) -> authorizationServer
				.oidc((oidc) -> oidc
					.logoutEndpoint(Customizer.withDefaults())
				)
				.authorizationEndpoint((authorizationEndpoint) -> authorizationEndpoint
					.consentPage("/consent")
				)
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
				.rpId("Spring Authorization Server.localhost")
				.allowedOrigins("http://localhost:9000")
				.disableDefaultRegistrationPage(true)
			)
			.addFilter(DefaultResourcesFilter.webauthn())
			.formLogin(Customizer.withDefaults())
			.oauth2Login(Customizer.withDefaults())
			.logout((logout) -> logout
				.logoutUrl("/logout")
				.logoutSuccessHandler(oidcLogoutSuccessHandler())
			);

		return http.build();
	}

	@Bean
	public OAuth2AuthorizationConsentService authorizationConsentService() {
		return new InMemoryOAuth2AuthorizationConsentService();
	}

	private LogoutSuccessHandler oidcLogoutSuccessHandler() {
		var oidcLogoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
			this.clientRegistrationRepository);
		oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/login?logout");

		return oidcLogoutSuccessHandler;
	}

}