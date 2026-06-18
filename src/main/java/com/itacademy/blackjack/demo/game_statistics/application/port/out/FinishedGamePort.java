package cat.itacademy.blackjack.game_statistics.application.port.out;

import cat.itacademy.blackjack.common.domain.value_object.GameId;
import cat.itacademy.blackjack.game_statistics.domain.model.FinishedGame;

import java.util.Optional;

public interface FinishedGamePort {
    void save(FinishedGame game);

    Optional<FinishedGame> findById(GameId id);

    Integer countGames();

    }
