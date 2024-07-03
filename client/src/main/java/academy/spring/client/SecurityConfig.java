package academy.spring.client;

import java.util.function.Function;

import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.R2dbcReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.TokenExchangeReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;

@Configuration
@EnableWebFluxSecurity
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

	@Bean
	public ReactiveOAuth2AuthorizedClientService authorizedClientService(
			DatabaseClient db, ReactiveClientRegistrationRepository clientRegistrationRepository) {

		return new R2dbcReactiveOAuth2AuthorizedClientService(db, clientRegistrationRepository);
	}

	@Bean
	public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
			ReactiveClientRegistrationRepository clientRegistrationRepository,
			ServerOAuth2AuthorizedClientRepository authorizedClientRepository,
			ReactiveOAuth2AuthorizedClientProvider tokenExchange) {

		ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
				ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
						.authorizationCode()
						.refreshToken()
						.provider(tokenExchange)
						.build();

		DefaultReactiveOAuth2AuthorizedClientManager authorizedClientManager =
				new DefaultReactiveOAuth2AuthorizedClientManager(
						clientRegistrationRepository, authorizedClientRepository);
		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

		return authorizedClientManager;
	}

	@Bean
	public TokenExchangeReactiveOAuth2AuthorizedClientProvider tokenExchange(
			ReactiveClientRegistrationRepository clientRegistrationRepository,
			ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {

		DefaultReactiveOAuth2AuthorizedClientManager authorizedClientManager =
				new DefaultReactiveOAuth2AuthorizedClientManager(
						clientRegistrationRepository, authorizedClientRepository);

		TokenExchangeReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
				new TokenExchangeReactiveOAuth2AuthorizedClientProvider();
		authorizedClientProvider.setSubjectTokenResolver(createTokenResolver(authorizedClientManager, "oidc-client"));

		return authorizedClientProvider;
	}

	private static Function<OAuth2AuthorizationContext, Mono<OAuth2Token>> createTokenResolver(
			ReactiveOAuth2AuthorizedClientManager authorizedClientManager, String clientRegistrationId) {

		return (context) -> {
			OAuth2AuthorizeRequest authorizeRequest =
					OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
							.principal(context.getPrincipal())
							.build();

			return authorizedClientManager.authorize(authorizeRequest)
					.map(OAuth2AuthorizedClient::getAccessToken);
		};
	}

}
