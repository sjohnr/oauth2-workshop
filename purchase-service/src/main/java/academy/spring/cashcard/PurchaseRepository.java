package academy.spring.cashcard;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends CrudRepository<Purchase, Long> {
	@NonNull
	List<Purchase> findByCashCardIdAndOwner(Long cashCardId, String owner);
}
