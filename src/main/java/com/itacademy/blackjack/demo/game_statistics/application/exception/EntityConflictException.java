package cat.itacademy.blackjack.game_statistics.application.exception;

import cat.itacademy.blackjack.common.domain.exception.BlackjackException;

public class EntityConflictException extends BlackjackException {
    public EntityConflictException(String message) {
        super(message);
    }
}
