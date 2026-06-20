package cat.itacademy.blackjack.demo.common.domain.exception;

public class GameException extends DomainException {
    public GameException(String message) {
        super("Game Error: " + message);
    }
}
