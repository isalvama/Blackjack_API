package cat.itacademy.blackjack.demo.finished_game.application.exception;

import cat.itacademy.blackjack.demo.common.application.exception.ApplicationException;

public class InvalidFinishedGameSearchException extends ApplicationException {
    public InvalidFinishedGameSearchException(String message) {
        super("Invalid Finished Game Search: " + message);
    }
}
