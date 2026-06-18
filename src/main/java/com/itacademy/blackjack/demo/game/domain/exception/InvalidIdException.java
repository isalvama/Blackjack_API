package cat.itacademy.blackjack.game.domain.exception;

import cat.itacademy.blackjack.common.domain.exception.DomainException;

public class InvalidIdException extends DomainException {
    public InvalidIdException(String message) {
        super("Invalid Id: " + message);
    }
}
