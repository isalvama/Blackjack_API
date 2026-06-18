package cat.itacademy.blackjack.game.domain.exception;

import cat.itacademy.blackjack.common.domain.exception.DomainException;

public class InvalidPlayerException extends DomainException {
    public InvalidPlayerException(String message) {
        super("Invalid Player: " + message);
    }
}
