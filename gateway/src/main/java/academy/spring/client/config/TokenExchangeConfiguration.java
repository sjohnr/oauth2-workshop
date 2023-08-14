package academy.spring.client.config;

import java.util.function.Function;

import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.TokenExchangeReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2Token;

@Configuration
public class TokenExchangeConfiguration {

	@Bean
	public TokenExchangeReactiveOAuth2AuthorizedClientProvider tokenExchange(
			ReactiveClientRegistrationRepository clientRegistrationRepository,
			ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {

		DefaultReactiveOAuth2AuthorizedClientManager authorizedClientManager =
				new DefaultReactiveOAuth2AuthorizedClientManager(
						clientRegistrationRepository, authorizedClientRepository);

		Function<OAuth2AuthorizationContext, Mono<OAuth2Token>> subjectTokenResolver =
			createTokenResolver(authorizedClientManager, "oidc-client");

		TokenExchangeReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
				new TokenExchangeReactiveOAuth2AuthorizedClientProvider();
		authorizedClientProvider.setSubjectTokenResolver(subjectTokenResolver);

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
