package academy.spring.cashcard;

/*
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc*/
class CashCardApplicationSecurityExercises {

	/*
	@Autowired
	MockMvc mvc;

	@Autowired
	JwtEncoder jwtEncoder;

	@Test
	public void _001_cashcardsRequiresAuthentication() throws Exception {
		this.mvc.perform(get("/cashcards")).andExpect(status().isUnauthorized());
	}

	@Test
	public void _002_cashcardsRequiresNonExpiredCredentials() throws Exception {
		String jwt = mint((claims) -> claims.expiresAt(Instant.now().minusSeconds(1000)));
		this.mvc.perform(get("/cashcards").header("Authorization", "Bearer " + jwt))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string(WWW_AUTHENTICATE, containsString("Jwt expired")));
		jwt = mint();
		this.mvc.perform(get("/cashcards").header("Authorization", "Bearer " + jwt))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$..owner").value(hasItem("steve")));
	}

	@WithMockUser(username = "steve", authorities = "SCOPE_cashcard:write")
	@Test
	public void _003_createCashCardStoresOwner() throws Exception {
		this.mvc.perform(post("/cashcards").with(csrf())
				.contentType("application/json")
				.content("""
					{
						"amount" : 250.00
					}
				"""))
				.andExpect(jsonPath("$.owner").value("steve"));
	}

	@Test
	public void _004_cashcardsRequiresReadAuthority() throws Exception {
		String jwt = mint((claims) -> claims.claim("scp", Collections.emptyList()));
		this.mvc.perform(get("/cashcards").header("Authorization", "Bearer " + jwt))
				.andExpect(status().isForbidden())
				.andExpect(header().string(WWW_AUTHENTICATE, containsString("requires higher privilege")));
		jwt = mint();
		this.mvc.perform(get("/cashcards").header("Authorization", "Bearer " + jwt))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$..owner").value(hasItem("steve")));
	}

	@WithMockUser(username = "steve", authorities="SCOPE_cashcard:read")
	@Test
	public void _005_cashcardsShowsOnlyOwnersCards() throws Exception {
		this.mvc.perform(get("/cashcards"))
				.andExpect(jsonPath("$..owner").value(hasItem("steve")))
				.andExpect(jsonPath("$..owner").value(not(hasItems("ria", "josh"))));
	}


	@WithMockUser(username = "steve", authorities="SCOPE_cashcard:read")
	@Test
	public void _006_cashcardReturnsOnlyOwnersCards() throws Exception {
		this.mvc.perform(get("/cashcards/102")).andExpect(status().isForbidden());
		this.mvc.perform(get("/cashcards/101")).andExpect(status().isOk());
	}

	@Test
	public void _007_cashcardsRequiresAudience() throws Exception {
		String jwt = mint((claims) -> claims.audience(Collections.emptyList()));
		this.mvc.perform(get("/cashcards").header("Authorization", "Bearer " + jwt))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string(WWW_AUTHENTICATE, containsString("aud claim is not valid")));
		jwt = mint();
		this.mvc.perform(get("/cashcards").header("Authorization", "Bearer " + jwt))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$..owner").value(hasItem("steve")));
	}

	@Test
	public void _008_cashcardsRequiresIssuer() throws Exception {
		String jwt = mint((claims) -> claims.issuer("http://wrong"));
		this.mvc.perform(get("/cashcards").header("Authorization", "Bearer " + jwt))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string(WWW_AUTHENTICATE, containsString("iss claim is not valid")));
		jwt = mint();
		this.mvc.perform(get("/cashcards").header("Authorization", "Bearer " + jwt))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$..owner").value(hasItem("steve")));
	}

	private String mint() {
		return mint((claims) -> {
		});
	}

	private String mint(Consumer<JwtClaimsSet.Builder> consumer) {
		JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
				.issuedAt(Instant.now().minusSeconds(100000))
				.expiresAt(Instant.now().plusSeconds(100000))
				.subject("steve")
				.issuer("http://localhost:9000")
				.audience(Arrays.asList("cashcard-client"))
				.claim("scp", Arrays.asList("cashcard:read", "cashcard:write"));
		consumer.accept(builder);
		JwtEncoderParameters parameters = JwtEncoderParameters.from(builder.build());
		return this.jwtEncoder.encode(parameters).getTokenValue();
	}

	@TestConfiguration
	static class JwtEncoderConfig {
		@Value("classpath:app.pub")
		RSAPublicKey pub;

		@Value("classpath:app.key")
		RSAPrivateKey priv;

		@Bean
		JwtEncoder jwtEncoder() {
			return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(new RSAKey.Builder(pub)
					.privateKey(this.priv).build())));
		}
	}*/
}
