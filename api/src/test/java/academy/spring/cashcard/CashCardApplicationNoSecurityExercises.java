package academy.spring.cashcard;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CashCardApplicationNoSecurityExercises {

	@Autowired
	MockMvc mvc;

	@Test
	public void _001_cashcardsRequiresAuthentication() throws Exception {
		this.mvc.perform(get("/cashcards")).andExpect(status().isUnauthorized());
	}

}
