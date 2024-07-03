package academy.spring.cashcard;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

@Service
public class CashCardService {

	private static final String CLIENT_REGISTRATION_ID = "token-client";

	private final RestClient restClient;

	private final OAuth2AuthorizedClientManager authorizedClientManager;

	public CashCardService(OAuth2AuthorizedClientManager authorizedClientManager) {
		this.restClient = RestClient.create("http://localhost:8091");
		this.authorizedClientManager = authorizedClientManager;
	}

	public List<CashCard> getCashCards(Authentication authentication) {
		OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authentication);

		CashCard[] cashCards = this.restClient.get()
				.uri("/cashcards")
				.headers((headers) -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
				.retrieve()
				.body(CashCard[].class);
		Assert.notNull(cashCards, "cashCards cannot be null");

		return Arrays.asList(cashCards);
	}

	public Optional<CashCard> getCashCard(Authentication authentication, Long id) {
		OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authentication);

		CashCard cashCard = this.restClient.get()
				.uri("/cashcards/{id}", id)
				.headers((headers) -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
				.retrieve()
				.body(CashCard.class);

		return Optional.ofNullable(cashCard);
	}

	public CashCard addCashCard(Authentication authentication, Double amount) {
		OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authentication);

		CashCard cashCard = new CashCard(null, amount, authentication.getName());
		this.restClient.post()
				.uri("/cashcards")
				.headers((headers) -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
				.body(cashCard)
				.retrieve()
				.body(CashCard.class);
		Assert.notNull(cashCard, "cashCard cannot be null");

		return cashCard;
	}

	private OAuth2AuthorizedClient getAuthorizedClient(Authentication authentication) {
		OAuth2AuthorizeRequest authorizeRequest =
				OAuth2AuthorizeRequest.withClientRegistrationId(CLIENT_REGISTRATION_ID)
						.principal(authentication)
						.build();

		return this.authorizedClientManager.authorize(authorizeRequest);
	}

}
