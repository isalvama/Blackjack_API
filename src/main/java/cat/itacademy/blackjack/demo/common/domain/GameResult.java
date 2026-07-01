package cat.itacademy.blackjack.demo.common.domain;

import cat.itacademy.blackjack.demo.common.domain.exception.InvalidGameResultException;

public enum GameResult {
    USER_WIN,
    DEALER_WIN,
    TIE;

    public static GameResult fromString (String gameResultString){
        try {
            return GameResult.valueOf(gameResultString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
           throw new InvalidGameResultException("the string " + gameResultString + " does not match any name of the constants of GameResult");
        }
    }
}