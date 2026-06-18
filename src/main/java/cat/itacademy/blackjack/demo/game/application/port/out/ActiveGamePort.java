package cat.itacademy.blackjack.demo.game.application.port.out;

import cat.itacademy.blackjack.demo.game.domain.model.Game;

public interface ActiveGamePort {
    Game saveGame(Game game);
}
