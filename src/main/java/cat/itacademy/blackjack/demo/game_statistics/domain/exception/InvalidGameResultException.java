package cat.itacademy.blackjack.demo.game_statistics.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class InvalidGameResultException extends DomainException {
    public InvalidGameResultException(String message) {
        super(message);
    }
}
