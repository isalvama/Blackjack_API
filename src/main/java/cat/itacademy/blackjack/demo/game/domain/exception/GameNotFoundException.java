package cat.itacademy.blackjack.demo.game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class GameNotFoundException extends DomainException {
    public GameNotFoundException(String message) {
        super("Game Not Found: " + message);
    }
}
