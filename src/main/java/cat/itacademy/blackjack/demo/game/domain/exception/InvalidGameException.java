package cat.itacademy.blackjack.demo.game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class InvalidGameException extends DomainException {
    public InvalidGameException(String message) {
        super("Invalid Game: " + message);
    }
}
