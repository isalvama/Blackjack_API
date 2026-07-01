package cat.itacademy.blackjack.demo.active_game.domain.exception;

public class InvalidHitException extends ActiveGameException {
    public InvalidHitException(String message) {
        super("Invalid hit: " + message);
    }
}
