package cat.itacademy.blackjack.demo.active_game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.GameException;

public class InvalidHitException extends GameException {
    public InvalidHitException(String message) {
        super("Invalid hit: " + message);
    }
}
