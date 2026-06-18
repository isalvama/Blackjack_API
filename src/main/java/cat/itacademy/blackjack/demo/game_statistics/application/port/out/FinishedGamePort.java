package cat.itacademy.blackjack.demo.game_statistics.application.port.out;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.game_statistics.domain.model.FinishedGame;

import java.util.Optional;

public interface FinishedGamePort {
    void save(FinishedGame game);

    Optional<FinishedGame> findById(GameId id);

    Integer countGames();

    }
