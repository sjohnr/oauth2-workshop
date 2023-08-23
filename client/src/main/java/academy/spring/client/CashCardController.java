package academy.spring.client;

import reactor.core.publisher.Mono;

import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@RestController
public class CashCardController {

	private final WebClient webClient;

	public CashCardController(WebClient.Builder webClientBuilder,
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2) {
		this.webClient = webClientBuilder
			.baseUrl("http://localhost:8090")
			.filter(oauth2)
			.build();
	}

	@GetMapping("/cashcards")
	public Mono<CashCard[]> getCashCards() {
		return this.webClient.get()
			.uri("/cashcards")
			.attributes(clientRegistrationId("oidc-client"))
			.retrieve()
			.bodyToMono(CashCard[].class);
	}

	record CashCard(Long id, Double amount, String owner) {
	}

}