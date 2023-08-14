package academy.spring.authserver.event;

import java.util.Base64;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.ldap.userdetails.InetOrgPerson;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

@Component
public class LdapAuthenticationEventListener {

	private final UserDetailsManager userDetailsManager;

	private final StringKeyGenerator keyGenerator = new Base64StringKeyGenerator(Base64.getEncoder(), 96);

	public LdapAuthenticationEventListener(UserDetailsManager userDetailsManager) {
		this.userDetailsManager = userDetailsManager;
	}

	@EventListener
	public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
		var authentication = event.getAuthentication();
		if (!(authentication.getPrincipal() instanceof OidcUser user)) {
			return;
		}

		// Create an external OIDC user in local LDAP to allow login with Passkeys
		if (!this.userDetailsManager.userExists(user.getName())) {
			var person = new InetOrgPerson.Essence();
			person.setUid(user.getSubject());
			person.setUsername(user.getName());
			person.setDn("uid=%s,ou=people,dc=springframework,dc=org".formatted(user.getName()));
			person.addCn(user.getName());
			person.setSn(user.getName());
			person.setAuthorities(AuthorityUtils.createAuthorityList("EXTERNAL_USER"));

			// Generate random password for external user
			person.setPassword(this.keyGenerator.generateKey());

			this.userDetailsManager.createUser(person.createUserDetails());
		}
	}

}
