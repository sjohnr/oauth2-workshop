package academy.spring.cashcard;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    private CashCardRepository cashcards;

    public CashCardController(CashCardRepository cashcards) {
        this.cashcards = cashcards;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        return this.cashcards.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CashCard> createCashCard(@RequestBody CashCard cashcard, UriComponentsBuilder ucb) {
        CashCard added = this.cashcards.save(new CashCard(null, cashcard.amount(), "user"));
        URI location = ucb
                .path("/cashcards/{id}")
                .buildAndExpand(added.id())
                .toUri();
        return ResponseEntity.created(location).body(added);
    }

    @GetMapping
    public ResponseEntity<Iterable<CashCard>> findAll() {
        return ResponseEntity.ok(this.cashcards.findAll());
    }
}
