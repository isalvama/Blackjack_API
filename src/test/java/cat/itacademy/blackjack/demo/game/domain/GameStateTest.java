package cat.itacademy.blackjack.demo.game.domain;

import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.common.domain.exception.InvalidGameResultException;
import cat.itacademy.blackjack.demo.game.domain.exception.InvalidGameStateException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {
    @Test
    void shouldCreateFromString() {
        String gameStateStr = GameState.STARTED.name();
        GameState gameState = GameState.fromString(gameStateStr);
        assertThat(gameState.toString()).isEqualTo(gameStateStr);
    }

    @Test
    void shouldCreateFromStringAnyCase() {
        String gameStateStr = "oVEr ";
        GameState gameState = GameState.fromString(gameStateStr);
        assertThat(gameState.toString()).isEqualTo(gameStateStr.toUpperCase().trim());
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        String invalidGameState = "invalid game result";
        Exception exception = assertThrows(InvalidGameStateException.class, () -> {
            GameState.fromString(invalidGameState);
        });
        assertTrue(exception.getMessage().contains("Invalid Game State"));
        assertTrue(exception.getMessage().contains("not match"));
        assertTrue(exception.getMessage().contains(invalidGameState));
    }
}