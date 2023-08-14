package academy.spring.cashcard;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseController {

	private final PurchaseService purchaseService;

	public PurchaseController(PurchaseService purchaseService) {
		this.purchaseService = purchaseService;
	}

	@GetMapping("/cashcards/{cashCardId}/purchases")
	public ResponseEntity<List<Purchase>> findByCashCardId(@PathVariable Long cashCardId, Authentication authentication) {
		var purchases = this.purchaseService.getPurchases(cashCardId, authentication.getName());
		return ResponseEntity.ok(purchases);
	}

}
