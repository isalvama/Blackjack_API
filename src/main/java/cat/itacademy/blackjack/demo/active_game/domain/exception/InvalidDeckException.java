package cat.itacademy.blackjack.demo.active_game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class InvalidDeckException extends DomainException {
    public InvalidDeckException(String message) {
        super("Invalid Deck: " + message);
    }
}
