package cat.itacademy.blackjack.game_statistics.domain.exception;

import cat.itacademy.blackjack.common.domain.exception.DomainException;

public class InvalidGameResultException extends DomainException {
    public InvalidGameResultException(String message) {
        super(message);
    }
}
