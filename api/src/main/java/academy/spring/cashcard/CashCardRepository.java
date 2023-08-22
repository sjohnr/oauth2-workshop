package academy.spring.cashcard;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

public interface CashCardRepository extends CrudRepository<CashCard, Long> {
	@Query("SELECT * FROM cash_card cc WHERE cc.owner = :#{authentication.name}")
	@NonNull
	Iterable<CashCard> findAll();
}
