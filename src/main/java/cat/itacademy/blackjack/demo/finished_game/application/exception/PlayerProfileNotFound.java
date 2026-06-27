package cat.itacademy.blackjack.demo.finished_game.application.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class PlayerProfileNotFound extends DomainException {
    public PlayerProfileNotFound(String message) {
        super("Player Profile Not Found: " + message);
    }
}
