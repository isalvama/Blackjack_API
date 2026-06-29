package cat.itacademy.blackjack.demo.finished_game.application.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class FinishedGameNotFoundException extends DomainException {
    public FinishedGameNotFoundException(String message) {
        super("Game Not Found: " + message);
    }
}
