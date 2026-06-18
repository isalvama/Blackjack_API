package cat.itacademy.blackjack.game_statistics.domain.exception;

import cat.itacademy.blackjack.common.domain.exception.DomainException;

public class InvalidFinishedGameException extends DomainException {
    public InvalidFinishedGameException(String message) {
        super(message);
    }
}
