package cat.itacademy.blackjack.demo.finished_game.application.exception;

import cat.itacademy.blackjack.demo.common.application.exception.EntityConflictException;

public class GameAlreadyExistsException extends EntityConflictException {
    public GameAlreadyExistsException(String id, String message) {
        super("Game Already Exists error: Game with id " + id + " already exists. " + message);
    }
}
