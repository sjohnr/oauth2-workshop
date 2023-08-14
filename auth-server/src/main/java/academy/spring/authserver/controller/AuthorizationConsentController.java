package academy.spring.authserver.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizationConsentController {

	private final RegisteredClientRepository registeredClientRepository;

	private final OAuth2AuthorizationConsentService authorizationConsentService;

	public AuthorizationConsentController(
			RegisteredClientRepository registeredClientRepository,
			OAuth2AuthorizationConsentService authorizationConsentService) {
		this.registeredClientRepository = registeredClientRepository;
		this.authorizationConsentService = authorizationConsentService;
	}

	@GetMapping("/consent")
	public String consent(
			@RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
			@RequestParam(OAuth2ParameterNames.SCOPE) String scope,
			@RequestParam(OAuth2ParameterNames.STATE) String state,
			Principal principal, Model model) {

		// Remove scopes that were already approved
		var registeredClient = this.registeredClientRepository.findByClientId(clientId);
		Assert.notNull(registeredClient, "registeredClient cannot be null");

		var authorizationConsent = this.authorizationConsentService.findById(
			registeredClient.getId(), principal.getName());
		var authorizedScopes = (authorizationConsent != null)
			? authorizationConsent.getScopes() : Collections.emptySet();

		var scopesToApprove = new HashSet<String>();
		var previouslyApprovedScopes = new HashSet<String>();
		for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
			if (OidcScopes.OPENID.equals(requestedScope)) {
				continue;
			}
			if (authorizedScopes.contains(requestedScope)) {
				previouslyApprovedScopes.add(requestedScope);
			} else {
				scopesToApprove.add(requestedScope);
			}
		}

		model.addAttribute("clientId", clientId);
		model.addAttribute("state", state);
		model.addAttribute("scopes", scopesToApprove);
		model.addAttribute("previouslyApprovedScopes", previouslyApprovedScopes);
		model.addAttribute("principalName", principal.getName());

		return "consent";
	}

}
