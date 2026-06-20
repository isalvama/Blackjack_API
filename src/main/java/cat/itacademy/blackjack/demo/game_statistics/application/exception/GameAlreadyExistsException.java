package cat.itacademy.blackjack.demo.game_statistics.application.exception;

public class GameAlreadyExistsException extends EntityConflictException {
    public GameAlreadyExistsException(String id, String message) {
        super("Game with id " + id + " already exists: " + message);
    }
}
