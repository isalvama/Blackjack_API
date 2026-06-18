package cat.itacademy.blackjack.game.domain.value_object;

import cat.itacademy.blackjack.game.domain.CardNumber;
import cat.itacademy.blackjack.game.domain.Suit;

public record Card(
        CardNumber cardNumber,
        Suit suit
)
{
}
