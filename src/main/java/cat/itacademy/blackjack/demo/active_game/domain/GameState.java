package cat.itacademy.blackjack.demo.active_game.domain;

import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidGameStateException;

public enum GameState {
    STARTED,
    OVER;

    public static GameState fromString(String gameState){
        try {
            return GameState.valueOf(gameState.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidGameStateException("Invalid Game State: the string " + gameState + " does not match any name of the constants of GameState");
        }
    }
}
