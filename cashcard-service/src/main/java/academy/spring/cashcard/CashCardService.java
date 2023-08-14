package academy.spring.cashcard;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class CashCardService {

	private final CashCardRepository cashCardRepository;

	public CashCardService(CashCardRepository cashCardRepository) {
		this.cashCardRepository = cashCardRepository;
	}

	public List<CashCard> getCashCards(String owner) {
		return this.cashCardRepository.findByOwner(owner);
	}

	public Optional<CashCard> getCashCard(Long id) {
		return this.cashCardRepository.findById(id);
	}

	public CashCard addCashCard(Double amount, String owner) {
		return this.cashCardRepository.save(new CashCard(null, amount, owner));
	}

}
