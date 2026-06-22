package cat.itacademy.blackjack.demo.finished_game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class InvalidFinishedGameException extends DomainException {
    public InvalidFinishedGameException(String message) {
        super(message);
    }
}
