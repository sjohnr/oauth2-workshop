package academy.spring.authserver.controller;

import java.util.Collections;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.webauthn.api.CredentialRecord;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebAuthnController {

	private final PublicKeyCredentialUserEntityRepository publicKeyCredentialUserEntityRepository;

	private final UserCredentialRepository userCredentialRepository;

	public WebAuthnController(PublicKeyCredentialUserEntityRepository publicKeyCredentialUserEntityRepository, UserCredentialRepository userCredentialRepository) {
		this.publicKeyCredentialUserEntityRepository = publicKeyCredentialUserEntityRepository;
		this.userCredentialRepository = userCredentialRepository;
	}

	@GetMapping("/webauthn/register")
	public String webauthn(Authentication authentication, HttpServletRequest request, Model model) {
		var credentials = Collections.<CredentialRecord>emptyList();

		var userEntity = this.publicKeyCredentialUserEntityRepository.findByUsername(authentication.getName());
		if (userEntity != null) {
			credentials = this.userCredentialRepository.findByUserId(userEntity.getId());
		}

		model.addAttribute("contextPath", request.getContextPath());
		model.addAttribute("credentials", credentials);

		return "webauthn-register";
	}

}
