package cat.itacademy.blackjack.demo.game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class GameException extends DomainException {
    public GameException(String message) {
        super("Game Error: " + message);
    }
}
