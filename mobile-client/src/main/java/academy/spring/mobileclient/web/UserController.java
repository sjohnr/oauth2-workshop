package academy.spring.mobileclient.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@GetMapping("/user")
	public UserResponse user(@AuthenticationPrincipal OidcUser user) {
		return new UserResponse(user.getSubject());
	}

	public record UserResponse(String username) {
	}

}
