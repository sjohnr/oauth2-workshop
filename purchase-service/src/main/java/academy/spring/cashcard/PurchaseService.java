package academy.spring.cashcard;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PurchaseService {

	private final PurchaseRepository purchaseRepository;

	public PurchaseService(PurchaseRepository purchaseRepository) {
		this.purchaseRepository = purchaseRepository;
	}

	public List<Purchase> getPurchases(Long cashCardId, String owner) {
		return this.purchaseRepository.findByCashCardIdAndOwner(cashCardId, owner);
	}

}
