package academy.spring.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.oauth2.client.R2dbcReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;

@SpringBootApplication
public class ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Bean
	public ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2(
			ReactiveClientRegistrationRepository clientRegistrationRepository,
			ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {

		return new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientRegistrationRepository, authorizedClientRepository);
	}

	@Bean
	public ReactiveOAuth2AuthorizedClientService authorizedClientService(
			DatabaseClient db,
			ReactiveClientRegistrationRepository clientRegistrationRepository) {

		return new R2dbcReactiveOAuth2AuthorizedClientService(db, clientRegistrationRepository);
	}

}
