package cat.itacademy.blackjack.demo.active_game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class InvalidIdException extends DomainException {
    public InvalidIdException(String message) {
        super("Invalid Id: " + message);
    }
}
