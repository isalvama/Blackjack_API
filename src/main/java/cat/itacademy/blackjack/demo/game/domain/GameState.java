package cat.itacademy.blackjack.demo.game.domain;

public enum GameState {
    STARTED,
    OVER;


    public static GameState fromString(String gameState) {
        return GameState.valueOf(gameState.toUpperCase());
    }
}
