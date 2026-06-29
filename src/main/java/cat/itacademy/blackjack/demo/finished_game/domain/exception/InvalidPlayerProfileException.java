package cat.itacademy.blackjack.demo.finished_game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class InvalidPlayerProfileException extends DomainException {
    public InvalidPlayerProfileException(String message) {
        super("Invalid PlayerProfile: " + message);
    }
}
