package cat.itacademy.blackjack.demo.common.domain.exception;

public class InvalidNameException extends DomainException {
    public InvalidNameException(String message) {
        super("Invalid Name: " + message);
    }
}
