package academy.spring.client;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public Greeting hello(Authentication authentication) {
		return new Greeting("Hello, " + authentication.getName());
	}

	public record Greeting(String message) {
	}

}
