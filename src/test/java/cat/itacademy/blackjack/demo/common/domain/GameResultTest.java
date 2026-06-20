package cat.itacademy.blackjack.demo.common.domain;

import cat.itacademy.blackjack.demo.common.domain.exception.InvalidGameResultException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GameResultTest {

    @Test
    void shouldCreateFromString() {
        String gameResultStr = GameResult.USER_WIN.name();
        GameResult gameResult = GameResult.fromString(gameResultStr);
        assertThat(gameResult.toString()).isEqualTo(gameResultStr);
    }

    @Test
    void shouldCreateFromStringAnyCase() {
        String gameResultStr = "tIe ";
        GameResult gameResult = GameResult.fromString(gameResultStr);
        assertThat(gameResult.toString()).isEqualTo(gameResultStr.toUpperCase().trim());
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        String invalidGameResult = "invalid game result";
        Exception exception = assertThrows(InvalidGameResultException.class, () -> {
            GameResult.fromString(invalidGameResult);
        });
        assertTrue(exception.getMessage().contains("Invalid Game Result"));
        assertTrue(exception.getMessage().contains("not match"));
        assertTrue(exception.getMessage().contains(invalidGameResult));
    }
}