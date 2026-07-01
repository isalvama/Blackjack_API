package cat.itacademy.blackjack.demo.active_game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class ActiveGameException extends DomainException {
    public ActiveGameException(String message) {
        super("Game Error: " + message);
    }
}
