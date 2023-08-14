package academy.spring.authserver.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String login(HttpServletRequest request, Model model) {
		model.addAttribute("contextPath", request.getContextPath());
		return "login";
	}

	@GetMapping("/logout")
	public String logout() {
		return "logout";
	}

}