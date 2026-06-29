package cat.itacademy.blackjack.demo.finished_game.application.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class PlayerProfileNotFoundException extends DomainException {
    public PlayerProfileNotFoundException(String message) {
        super("Player Profile Not Found: " + message);
    }
}
