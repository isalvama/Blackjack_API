package cat.itacademy.blackjack.demo.finished_game.domain.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.DomainException;

public class InvalidFinishedGameSearch extends DomainException {
    public InvalidFinishedGameSearch(String message) {
        super("Invalid Finished Game Search: " + message);
    }
}
