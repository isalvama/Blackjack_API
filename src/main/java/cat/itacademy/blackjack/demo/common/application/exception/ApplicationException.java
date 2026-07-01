package cat.itacademy.blackjack.demo.common.application.exception;

import cat.itacademy.blackjack.demo.common.domain.exception.BlackjackException;

public class ApplicationException extends BlackjackException {
    public ApplicationException(String message) {
        super(message);
    }
}
