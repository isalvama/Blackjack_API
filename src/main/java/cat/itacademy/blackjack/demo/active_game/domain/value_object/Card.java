package cat.itacademy.blackjack.demo.active_game.domain.value_object;

import cat.itacademy.blackjack.demo.active_game.domain.CardNumber;
import cat.itacademy.blackjack.demo.active_game.domain.Suit;

public record Card(
        CardNumber cardNumber,
        Suit suit
)
{
}
