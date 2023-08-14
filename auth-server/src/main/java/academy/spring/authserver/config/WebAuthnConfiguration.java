package academy.spring.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.webauthn.management.MapPublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.MapUserCredentialRepository;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;

@Configuration
public class WebAuthnConfiguration {

	@Bean
	public PublicKeyCredentialUserEntityRepository publicKeyCredentialUserEntityRepository() {
		return new MapPublicKeyCredentialUserEntityRepository();
	}

	@Bean
	public UserCredentialRepository userCredentialRepository() {
		return new MapUserCredentialRepository();
	}

}
