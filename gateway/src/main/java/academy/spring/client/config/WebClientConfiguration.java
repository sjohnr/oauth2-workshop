package academy.spring.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

	@Bean
	public WebClient webClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager, WebClient.Builder builder) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction exchangeFilterFunction =
			new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

		return builder.filter(exchangeFilterFunction).build();
	}

}
