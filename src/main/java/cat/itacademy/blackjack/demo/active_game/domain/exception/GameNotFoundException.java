package cat.itacademy.blackjack.demo.active_game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class GameNotFoundException extends DomainException {
    public GameNotFoundException(String id) {
        super(String.format("Game with id %s not found ", id));
    }
}
