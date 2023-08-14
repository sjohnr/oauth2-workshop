package academy.spring.authserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home() {
		return "redirect:/account";
	}

	@GetMapping("/account")
	public String account() {
		return "account";
	}

}
