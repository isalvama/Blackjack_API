package cat.itacademy.blackjack.demo.active_game.application.exception;

import cat.itacademy.blackjack.demo.common.application.exception.NotFoundException;

public class ActiveGameNotFoundException extends NotFoundException {
    public ActiveGameNotFoundException(String id) {
        super(String.format("Game with id %s not found ", id));
    }
}
