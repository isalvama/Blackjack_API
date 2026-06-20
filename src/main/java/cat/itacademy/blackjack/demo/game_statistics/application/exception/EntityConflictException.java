package cat.itacademy.blackjack.demo.game_statistics.application.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.BlackjackException;

public class EntityConflictException extends BlackjackException {
    public EntityConflictException(String message) {
        super(message);
    }
}
