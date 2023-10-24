package academy.spring.mobileclient.web;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ServerWebExchange;

@Controller
public class LoginController {

	@GetMapping("/login")
	public ResponseEntity<Void> login(ServerWebExchange exchange) {
		return ResponseEntity.status(HttpStatus.FOUND)
			.location(URI.create("/oauth2/authorization/oidc-client"))
			.build();
	}

}
