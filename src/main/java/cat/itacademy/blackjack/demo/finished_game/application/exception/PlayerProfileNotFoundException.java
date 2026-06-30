package cat.itacademy.blackjack.demo.finished_game.application.exception;

import cat.itacademy.blackjack.demo.common.application.exception.NotFoundException;

public class PlayerProfileNotFoundException extends NotFoundException {
    public PlayerProfileNotFoundException(String message) {
        super("Player Profile Not Found: " + message);
    }
}
