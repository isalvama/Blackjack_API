package cat.itacademy.blackjack.demo.shuffle_strategy;

import cat.itacademy.blackjack.demo.active_game.domain.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.demo.active_game.domain.CardNumber;
import cat.itacademy.blackjack.demo.active_game.domain.Suit;
import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;

import java.util.List;

public class PlayerWinningByCardsValueStrategy implements ShuffleStrategy {
    private static final Suit CLUBS = Suit.CLUBS;
    private static final Suit SPADES = Suit.SPADES;
    private static final Suit HEARTS = Suit.HEARTS;

    private static final CardNumber ACE = CardNumber.ACE;
    private static final CardNumber JACK = CardNumber.JACK;
    private static final CardNumber NINE = CardNumber.NINE;
    private static final CardNumber TWO = CardNumber.TWO;


    @Override
    public List<Card> shuffle(List<Card> cards) {

        cards.removeIf(card ->
                card.cardNumber().equals(NINE) && card.suit().equals(CLUBS));
        cards.removeIf(card -> card.cardNumber().equals(JACK) && card.suit().equals(SPADES));
        cards.removeIf(card -> card.cardNumber().equals(NINE) && card.suit().equals(HEARTS));


        cards.removeIf(card -> card.cardNumber().equals(NINE) && card.suit().equals(SPADES));

        cards.add(0, new Card(NINE, CLUBS));
        cards.add(1, new Card(JACK, SPADES));
        cards.add(2, new Card(NINE, HEARTS));
        cards.add(3, new Card(NINE, SPADES));
        return cards;
    }
}
