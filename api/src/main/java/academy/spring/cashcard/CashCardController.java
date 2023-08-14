package academy.spring.cashcard;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
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

    private final CashCardService cashCardService;

    public CashCardController(CashCardService cashCardService) {
        this.cashCardService = cashCardService;
    }

    @GetMapping("/{requestedId}")
    @PostAuthorize("returnObject.body.owner == authentication.name")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        return this.cashCardService.getCashCard(requestedId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CashCard> createCashCard(
            @RequestBody CashCard requestBody,
            UriComponentsBuilder uriComponentsBuilder,
            Authentication authentication) {

        CashCard responseBody = this.cashCardService.addCashCard(
                requestBody.amount(), authentication.getName());

        URI location = uriComponentsBuilder
                .path("/cashcards/{id}")
                .buildAndExpand(responseBody.id())
                .toUri();

        return ResponseEntity.created(location).body(responseBody);
    }

    @GetMapping
    public ResponseEntity<List<CashCard>> findAll(Authentication authentication) {
        return ResponseEntity.ok(this.cashCardService.getCashCards(authentication.getName()));
    }

}
