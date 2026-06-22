package cat.itacademy.blackjack.demo.active_game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class InvalidPlayerException extends DomainException {
    public InvalidPlayerException(String message) {
        super("Invalid Player: " + message);
    }
}
