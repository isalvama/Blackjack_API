package cat.itacademy.blackjack.game.domain.exception;

import cat.itacademy.blackjack.common.domain.exception.DomainException;

public class InvalidDeckException extends DomainException {
    public InvalidDeckException(String message) {
        super("Invalid Deck: " + message);
    }
}
