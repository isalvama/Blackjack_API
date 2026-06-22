package cat.itacademy.blackjack.demo.shuffle_strategy;

import cat.itacademy.blackjack.demo.game.application.service.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.demo.game.domain.CardNumber;
import cat.itacademy.blackjack.demo.game.domain.Suit;
import cat.itacademy.blackjack.demo.game.domain.value_object.Card;

import java.util.List;


public class PlayerLosingByExceeding21Strategy implements ShuffleStrategy {
    private static final Suit CLUBS = Suit.CLUBS;
    private static final Suit HEARTS = Suit.HEARTS;
    private static final Suit DIAMONDS = Suit.DIAMONDS;
    private static final CardNumber JACK = CardNumber.JACK;

    @Override
    public List<Card> shuffle(List<Card> cards) {
        cards.removeIf(card ->
                card.cardNumber().equals(CardNumber.EIGHT) && card.suit().equals(DIAMONDS));

        cards.removeIf(card ->
                card.cardNumber().equals(JACK) && card.suit().equals(HEARTS));

        cards.removeIf(card -> card.cardNumber().equals(JACK) && card.suit().equals(CLUBS));

        cards.removeIf(card -> card.cardNumber().equals(CardNumber.KING) && card.suit().equals(DIAMONDS));

        cards.add(0, new Card(CardNumber.EIGHT, DIAMONDS));
        cards.add(1, new Card(JACK, HEARTS));
        cards.add(2, new Card(JACK, CLUBS));
        cards.add(3, new Card(CardNumber.KING, DIAMONDS));
        return cards;
    }
}
