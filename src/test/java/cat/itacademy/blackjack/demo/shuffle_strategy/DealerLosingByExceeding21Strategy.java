package cat.itacademy.blackjack.demo.shuffle_strategy;

import cat.itacademy.blackjack.demo.active_game.application.service.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.demo.active_game.domain.CardNumber;
import cat.itacademy.blackjack.demo.active_game.domain.Suit;
import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;

import java.util.List;

import static cat.itacademy.blackjack.demo.active_game.domain.CardNumber.SIX;

public class DealerLosingByExceeding21Strategy implements ShuffleStrategy {
    private static final Suit CLUBS = Suit.CLUBS;
    private static final Suit SPADES = Suit.SPADES;
    private static final Suit HEARTS = Suit.HEARTS;

    private static final CardNumber TWO = CardNumber.TWO;
    private static final CardNumber EIGHT = CardNumber.EIGHT;
    private static final CardNumber JACK = CardNumber.JACK;
    private static final CardNumber NINE = CardNumber.NINE;


    @Override
    public List<Card> shuffle(List<Card> cards) {
        cards.removeIf(card -> card.cardNumber().equals(JACK) && card.suit().equals(SPADES));
        cards.removeIf(card ->
                card.cardNumber().equals(SIX) && card.suit().equals(CLUBS));
        cards.removeIf(card -> card.cardNumber().equals(JACK) && card.suit().equals(HEARTS));
        cards.removeIf(card -> card.cardNumber().equals(SIX) && card.suit().equals(SPADES));
        cards.removeIf(card -> card.cardNumber().equals(NINE) && card.suit().equals(SPADES));

        cards.add(0, new Card(JACK, SPADES));
        cards.add(1, new Card(SIX, CLUBS));
        cards.add(2, new Card(JACK, HEARTS));
        cards.add(3, new Card(SIX, SPADES));
        cards.add(4, new Card(NINE, SPADES));

        return cards;
    }
}
