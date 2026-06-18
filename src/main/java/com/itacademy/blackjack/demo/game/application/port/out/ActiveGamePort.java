package cat.itacademy.blackjack.game.application.port.out;

import cat.itacademy.blackjack.game.domain.model.Game;

public interface ActiveGamePort {
    Game saveGame(Game game);
}
