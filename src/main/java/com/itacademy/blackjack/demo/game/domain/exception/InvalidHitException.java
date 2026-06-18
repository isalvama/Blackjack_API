package cat.itacademy.blackjack.game.domain.exception;

public class InvalidHitException extends GameException {
    public InvalidHitException(String message) {
        super("Invalid hit: " + message);
    }
}
