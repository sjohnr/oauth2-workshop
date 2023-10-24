package academy.spring.mobileclient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.bouncycastle.util.encoders.Hex;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

public class InMemoryServerSecurityContextRepository implements ServerSecurityContextRepository {

	public static final String SECURITY_CONTEXT_ID_ATTR_NAME = ServerSecurityContextRepository.class.getName() + ".SECURITY_CONTEXT_ID";

	private final Map<String, SecurityContext> securityContexts = new ConcurrentHashMap<>();

	private Function<ServerWebExchange, String> securityContextIdResolver = new BearerTokenResolver();

	private StringKeyGenerator keyGenerator = createKeyGenerator();

	@Override
	public Mono<Void> save(ServerWebExchange exchange, SecurityContext securityContext) {
		return Mono.fromRunnable(() -> saveSecurityContext(exchange, securityContext));
	}

	private void saveSecurityContext(ServerWebExchange exchange, SecurityContext securityContext) {
		if (securityContext != null) {
			String securityContextId = this.keyGenerator.generateKey();
			this.securityContexts.put(securityContextId, securityContext);
			exchange.getAttributes().put(SECURITY_CONTEXT_ID_ATTR_NAME, securityContextId);
		} else {
			String securityContextId = this.securityContextIdResolver.apply(exchange);
			Assert.hasText(securityContextId, "securityContextId cannot be empty");
			this.securityContexts.remove(securityContextId);
		}
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange exchange) {
		return Mono.fromCallable(() -> loadSecurityContext(exchange));
	}

	private SecurityContext loadSecurityContext(ServerWebExchange exchange) {
		String securityContextId = this.securityContextIdResolver.apply(exchange);
		return StringUtils.hasText(securityContextId) ? this.securityContexts.get(securityContextId) : null;
	}

	/**
	 * Set the {@code Function} used to extract the {@code SECURITY_CONTEXT_ID} from the request.
	 * @param securityContextIdResolver the {@code Function} used to extract the token from the request
	 */
	public void setSecurityContextIdResolver(Function<ServerWebExchange, String> securityContextIdResolver) {
		Assert.notNull(securityContextIdResolver, "securityContextIdResolver cannot be null");
		this.securityContextIdResolver = securityContextIdResolver;
	}

	/**
	 * The {@link StringKeyGenerator} used to generate the {@code SECURITY_CONTEXT_ID}.
	 * @param keyGenerator the {@link StringKeyGenerator} used to generate the token
	 */
	public void setKeyGenerator(StringKeyGenerator keyGenerator) {
		Assert.notNull(keyGenerator, "keyGenerator cannot be null");
		this.keyGenerator = keyGenerator;
	}

	private static StringKeyGenerator createKeyGenerator() {
		BytesKeyGenerator bytesKeyGenerator = KeyGenerators.secureRandom(16);
		return () -> new String(Hex.encode(bytesKeyGenerator.generateKey())).toUpperCase();
	}

	static class BearerTokenResolver implements Function<ServerWebExchange, String> {

		@Override
		public String apply(ServerWebExchange exchange) {
			String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
			return StringUtils.startsWithIgnoreCase(authorizationHeader, "Bearer ") ? authorizationHeader.substring("Bearer ".length()) : null;
		}

	}

}
