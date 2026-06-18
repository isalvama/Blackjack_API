package cat.itacademy.blackjack.demo.game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class InvalidPlayerException extends DomainException {
    public InvalidPlayerException(String message) {
        super("Invalid Player: " + message);
    }
}
