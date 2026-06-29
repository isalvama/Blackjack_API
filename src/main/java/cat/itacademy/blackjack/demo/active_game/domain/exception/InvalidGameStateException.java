package cat.itacademy.blackjack.demo.active_game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class InvalidGameStateException extends DomainException {
    public InvalidGameStateException(String message) {
        super(message);
    }
}
