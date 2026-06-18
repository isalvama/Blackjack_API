package cat.itacademy.blackjack.demo.game.domain.value_object;

import cat.itacademy.blackjack.demo.game.domain.CardNumber;
import cat.itacademy.blackjack.demo.game.domain.Suit;

public record Card(
        CardNumber cardNumber,
        Suit suit
)
{
}
