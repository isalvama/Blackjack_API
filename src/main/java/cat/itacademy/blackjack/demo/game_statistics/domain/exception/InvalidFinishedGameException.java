package cat.itacademy.blackjack.demo.game_statistics.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class InvalidFinishedGameException extends DomainException {
    public InvalidFinishedGameException(String message) {
        super(message);
    }
}
