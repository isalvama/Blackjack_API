package cat.itacademy.blackjack.demo.common.domain.exception;

public class InvalidGameResultException extends DomainException {
    public InvalidGameResultException(String message) {
        super("Invalid Game Result: " + message);
    }
}
