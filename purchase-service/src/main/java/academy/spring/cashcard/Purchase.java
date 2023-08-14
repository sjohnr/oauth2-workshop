package academy.spring.cashcard;

import org.springframework.data.annotation.Id;

public record Purchase(@Id Long id, Long cashCardId, String description, Double amount, String owner) {
}
