package cat.itacademy.blackjack.demo.game.application.port.out;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.game.domain.model.Game;

import java.util.Optional;

public interface ActiveGamePort {
    Game saveGame(Game game);
    Optional<Game> getGame(GameId id);
}
