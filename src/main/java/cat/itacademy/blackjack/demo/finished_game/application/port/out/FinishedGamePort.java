package cat.itacademy.blackjack.demo.finished_game.application.port.out;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;

import java.util.Optional;

public interface FinishedGamePort {
    void save(FinishedGame game);

    Optional<FinishedGame> findById(GameId id);

    Integer countGames();

    }
