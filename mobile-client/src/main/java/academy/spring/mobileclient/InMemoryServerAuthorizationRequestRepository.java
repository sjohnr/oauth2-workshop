package academy.spring.mobileclient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import reactor.core.publisher.Mono;

import org.springframework.security.oauth2.client.web.server.ServerAuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.web.server.ServerWebExchange;

public class InMemoryServerAuthorizationRequestRepository implements ServerAuthorizationRequestRepository<OAuth2AuthorizationRequest> {

	private final Map<String, OAuth2AuthorizationRequest> requests = new ConcurrentHashMap<>();

	@Override
	public Mono<Void> saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, ServerWebExchange exchange) {
		return Mono.fromRunnable(() -> saveAuthorizationRequestByState(authorizationRequest));
	}

	private void saveAuthorizationRequestByState(OAuth2AuthorizationRequest authorizationRequest) {
		this.requests.put(authorizationRequest.getState(), authorizationRequest);
	}

	@Override
	public Mono<OAuth2AuthorizationRequest> loadAuthorizationRequest(ServerWebExchange exchange) {
		return Mono.fromCallable(() -> loadAuthorizationRequestByState(exchange));
	}

	private OAuth2AuthorizationRequest loadAuthorizationRequestByState(ServerWebExchange exchange) {
		String state = exchange.getRequest().getQueryParams().getFirst(OAuth2ParameterNames.STATE);
		return this.requests.get(state);
	}

	@Override
	public Mono<OAuth2AuthorizationRequest> removeAuthorizationRequest(ServerWebExchange exchange) {
		return Mono.fromCallable(() -> removeAuthorizationRequestByState(exchange));
	}

	private OAuth2AuthorizationRequest removeAuthorizationRequestByState(ServerWebExchange exchange) {
		String state = exchange.getRequest().getQueryParams().getFirst(OAuth2ParameterNames.STATE);
		return this.requests.remove(state);
	}

}
