package cat.itacademy.blackjack.demo.finished_game.application.exception;

import cat.itacademy.blackjack.demo.common.application.exception.NotFoundException;

public class FinishedGameNotFoundException extends NotFoundException {
    public FinishedGameNotFoundException(String message) {
        super("Game Not Found: " + message);
    }
}
