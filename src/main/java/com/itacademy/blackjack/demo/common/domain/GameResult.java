package cat.itacademy.blackjack.common.domain;

import cat.itacademy.blackjack.game_statistics.domain.exception.InvalidGameResultException;

public enum GameResult {
    USER_WIN,
    DEALER_WIN,
    TIE;

    public static GameResult from (String gameResultString){
        try {
            return GameResult.valueOf(gameResultString.toUpperCase());
        } catch (IllegalArgumentException e) {
           throw new InvalidGameResultException("Invalid Game Result: " + e.getMessage());
        }
    }
}