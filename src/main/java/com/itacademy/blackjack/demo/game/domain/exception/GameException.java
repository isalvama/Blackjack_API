package cat.itacademy.blackjack.game.domain.exception;

import cat.itacademy.blackjack.common.domain.exception.DomainException;

public class GameException extends DomainException {
    public GameException(String message) {
        super("Game Error: " + message);
    }
}
