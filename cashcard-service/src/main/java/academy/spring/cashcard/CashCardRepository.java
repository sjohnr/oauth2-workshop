package academy.spring.cashcard;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface CashCardRepository extends CrudRepository<CashCard, Long> {
	@NonNull
	List<CashCard> findByOwner(String owner);
}
