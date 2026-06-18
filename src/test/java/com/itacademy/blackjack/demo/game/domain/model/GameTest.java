package cat.itacademy.blackjack.game.domain.model;

import cat.itacademy.blackjack.common.domain.value_object.Name;
import org.junit.jupiter.api.Test;

class GameTest {

    @Test
    void shouldCreate() {
        UserPlayer userPlayer = UserPlayer.create(Name.of("name"));
        Dealer dealer = Dealer.create();
        Deck deck = Deck.create();
        Game game = Game.create(userPlayer, dealer, deck);

    }
}