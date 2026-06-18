package cat.itacademy.blackjack.game_statistics.domain.exception;

import cat.itacademy.blackjack.common.domain.exception.DomainException;

public class InvalidPlayerProfileException extends DomainException {
    public InvalidPlayerProfileException(String message) {
        super("Invalid PlayerProfile: " + message);
    }
}
