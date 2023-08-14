package academy.spring.client.controller;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.view.Rendering;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Controller
public class CashCardsController {

	private final WebClient webClient;

	public CashCardsController(WebClient webClient) {
		this.webClient = webClient;
	}

	@GetMapping("/my/cashcards")
	public Mono<Rendering> cashcards() {
		var cashcards = this.webClient.get()
			.uri("http://localhost:8090/cashcards")
			.attributes(clientRegistrationId("gateway-client"))
			.retrieve()
			.bodyToFlux(CashCard.class)
			.collectList();

		var view = Rendering.view("cashcards")
			.modelAttribute("cashcards", cashcards)
			.build();

		return Mono.just(view);
	}

	@GetMapping("/my/cashcards/{id}")
	public Mono<Rendering> purchases(@PathVariable("id") Long id, Model model) {
		var purchases = this.webClient.get()
			.uri("http://localhost:8091/cashcards/{id}/purchases", id)
			.attributes(clientRegistrationId("gateway-client"))
			.retrieve()
			.bodyToFlux(Purchase.class)
			.collectList();

		var view = Rendering.view("purchases")
			.modelAttribute("purchases", purchases)
			.build();

		return Mono.just(view);
	}

	public record CashCard(Long id, Double amount, String owner) {
	}

	public record Purchase(Long id, Long cashCardId, String description, Double amount, String owner) {
	}

}
