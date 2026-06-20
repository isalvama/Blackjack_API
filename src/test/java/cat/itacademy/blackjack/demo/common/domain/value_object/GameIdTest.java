package cat.itacademy.blackjack.demo.common.domain.value_object;

import cat.itacademy.blackjack.demo.game.domain.exception.InvalidIdException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GameIdTest {
    @Test
    void shouldCreateGameIdWhenUUIDIsValid() {
        UUID uuid = UUID.randomUUID();
        GameId gameId = new GameId(uuid);
        assertThat(gameId.value()).isEqualTo(uuid);
    }

    @Test
    void shouldThrowExceptionWhenNull() {
        Exception exception = assertThrows(InvalidIdException.class, () -> {
            new GameId(null);
        });
        assertTrue(exception.getMessage().contains("GameId"));
        assertTrue(exception.getMessage().contains("null"));
    }

    @Test
    void shouldGenerateRandom() {
        GameId id1 = GameId.generate();
        GameId id2 = GameId.generate();

        assertThat(id1).isNotNull();
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void shouldCreateFromString() {
        String uuidStr = UUID.randomUUID().toString();
        GameId gameId = GameId.fromString(uuidStr);
        assertThat(gameId.toString()).isEqualTo(uuidStr);
    }

    @Test
    void shouldCreateFromUUID() {
        UUID uuid = UUID.randomUUID();
        GameId gameId = GameId.fromUUID(uuid);
        assertThat(gameId.value()).isEqualTo(uuid);
    }

    @Test
    void shouldBeEqual() {
        UUID uuid = UUID.randomUUID();
        GameId id1 = GameId.fromUUID(uuid);
        GameId id2 = GameId.fromUUID(uuid);

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}