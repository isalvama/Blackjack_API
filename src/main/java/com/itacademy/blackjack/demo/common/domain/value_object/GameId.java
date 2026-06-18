package cat.itacademy.blackjack.common.domain.value_object;

import cat.itacademy.blackjack.game.domain.exception.InvalidIdException;

import java.util.UUID;

public record GameId (UUID value) {

    public GameId {
        if (value == null) {
            throw new InvalidIdException("GameId value must not be null");
        }
    }

    public static GameId generate() {
        return new GameId(UUID.randomUUID());
    }

    public static GameId fromString(String value) {
        return new GameId(UUID.fromString(value));
    }

    public static GameId fromUUID(UUID value) {
        return new GameId(value);
    }

    @Override
    public String toString(){
        return value.toString();
    }
}

