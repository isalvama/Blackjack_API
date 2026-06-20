package cat.itacademy.blackjack.demo.game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class InvalidHandException extends DomainException {
    public InvalidHandException(String message) {
        super("Invalid Hand: " + message);
    }
}
