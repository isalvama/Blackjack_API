package cat.itacademy.blackjack.demo.shuffle_strategy;

import cat.itacademy.blackjack.demo.game.application.service.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.demo.game.domain.CardNumber;
import cat.itacademy.blackjack.demo.game.domain.Suit;
import cat.itacademy.blackjack.demo.game.domain.value_object.Card;

import java.util.List;

public class GameWithoutBlackJackStrategy implements ShuffleStrategy {
    private static final Suit DIAMONDS = Suit.DIAMONDS;
    private static final Suit SPADES = Suit.SPADES;
    private static final CardNumber TWO = CardNumber.TWO;
    private static final CardNumber THREE = CardNumber.THREE;

    @Override
    public List<Card> shuffle(List<Card> cards) {
        cards.removeIf(c -> c.cardNumber().equals(TWO) && c.suit().equals(DIAMONDS));
        cards.removeIf(c -> c.cardNumber().equals(THREE) && c.suit().equals(SPADES));

        cards.add(0, new Card(TWO, DIAMONDS));
        cards.add(1, new Card(THREE, SPADES));

        return cards;
    }
}
