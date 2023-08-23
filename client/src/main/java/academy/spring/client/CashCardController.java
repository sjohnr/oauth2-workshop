package academy.spring.client;

import reactor.core.publisher.Mono;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
class CashCardController {

	private final WebClient webClient;

	CashCardController(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder
			.baseUrl("http://localhost:8090")
			.build();
	}

	@GetMapping("/cashcards")
	Mono<CashCard[]> getCashCards(
		@RegisteredOAuth2AuthorizedClient("oidc-client")
		OAuth2AuthorizedClient authorizedClient) {

		String accessToken = authorizedClient.getAccessToken().getTokenValue();
		return this.webClient.get()
			.uri("/cashcards")
			.headers(headers -> headers.setBearerAuth(accessToken))
			.retrieve()
			.bodyToMono(CashCard[].class);
	}

	record CashCard(Long id, Double amount, String owner) {
	}

}