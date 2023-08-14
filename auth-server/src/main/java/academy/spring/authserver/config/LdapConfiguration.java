package academy.spring.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.ldap.DefaultLdapUsernameToDnMapper;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.server.UnboundIdContainer;
import org.springframework.security.ldap.userdetails.LdapUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class LdapConfiguration {

	@Bean
	public UnboundIdContainer ldapContainer() {
		UnboundIdContainer result = new UnboundIdContainer("dc=springframework,dc=org", "classpath:users.ldif");
		result.setPort(0);

		return result;
	}

	@Bean
	public DefaultSpringSecurityContextSource contextSource(UnboundIdContainer container) {
		var providerUrl = "ldap://localhost:%d/dc=springframework,dc=org".formatted(container.getPort());
		return new DefaultSpringSecurityContextSource(providerUrl);
	}

	@Bean
	public UserDetailsManager userDetailsManager(ContextSource contextSource) {
		var userDetailsManager = new LdapUserDetailsManager(contextSource);
		userDetailsManager.setGroupSearchBase("ou=groups");
		userDetailsManager.setGroupMemberAttributeName("member");

		var usernameMapper = new DefaultLdapUsernameToDnMapper("ou=people", "uid");
		userDetailsManager.setUsernameMapper(usernameMapper);

		return userDetailsManager;
	}

	@Bean
	public BindAuthenticator authenticator(BaseLdapPathContextSource contextSource) {
		var authenticator = new BindAuthenticator(contextSource);
		authenticator.setUserDnPatterns(new String[] { "uid={0},ou=people" });

		return authenticator;
	}

	@Bean
	public LdapAuthenticationProvider authenticationProvider(LdapAuthenticator authenticator) {
		return new LdapAuthenticationProvider(authenticator);
	}

}
