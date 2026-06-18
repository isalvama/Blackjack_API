package cat.itacademy.blackjack.game.domain.exception;

import cat.itacademy.blackjack.common.domain.exception.DomainException;

public class InvalidGameException extends DomainException {
    public InvalidGameException(String message) {
        super("Invalid Game: " + message);
    }
}
