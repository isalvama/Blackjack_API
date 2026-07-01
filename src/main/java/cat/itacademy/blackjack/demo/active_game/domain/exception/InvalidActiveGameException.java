package cat.itacademy.blackjack.demo.active_game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class InvalidActiveGameException extends DomainException {
    public InvalidActiveGameException(String message) {
        super("Invalid Game: " + message);
    }
}
