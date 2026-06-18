package cat.itacademy.blackjack.demo.game.domain.exception;

public class InvalidHitException extends GameException {
    public InvalidHitException(String message) {
        super("Invalid hit: " + message);
    }
}
