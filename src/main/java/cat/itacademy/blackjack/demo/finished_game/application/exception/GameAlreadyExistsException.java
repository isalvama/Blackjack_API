package cat.itacademy.blackjack.demo.finished_game.application.exception;

public class GameAlreadyExistsException extends EntityConflictException {
    public GameAlreadyExistsException(String id, String message) {
        super("Game with id " + id + " already exists: " + message);
    }
}
