package academy.spring.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootApplication
public class AuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails steve = User.withDefaultPasswordEncoder()
			.username("steve")
			.password("password")
			.roles("USER")
			.build();
		UserDetails ria = User.withDefaultPasswordEncoder()
			.username("ria")
			.password("password")
			.roles("USER")
			.build();
		UserDetails josh = User.withDefaultPasswordEncoder()
			.username("josh")
			.password("password")
			.roles("USER")
			.build();
		return new InMemoryUserDetailsManager(steve, ria, josh);
	}

}
