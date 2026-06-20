package cat.itacademy.blackjack.demo.shuffle_strategy;

import cat.itacademy.blackjack.demo.game.application.service.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.demo.game.domain.CardNumber;
import cat.itacademy.blackjack.demo.game.domain.Suit;
import cat.itacademy.blackjack.demo.game.domain.value_object.Card;

import java.util.List;

import static cat.itacademy.blackjack.demo.game.domain.CardNumber.KING;

public class TieWithBlackjackShuffleStrategy implements ShuffleStrategy {
    private static final Suit CLUBS = Suit.CLUBS;
    private static final Suit HEARTS = Suit.HEARTS;
    private static final CardNumber ACE = CardNumber.ACE;
    private static final CardNumber JACK = CardNumber.JACK;


    @Override
    public List<Card> shuffle(List<Card> cards) {

        cards.removeIf(card ->
                card.cardNumber().equals(ACE) && card.suit().equals(CLUBS));

        cards.removeIf(card ->
                card.cardNumber().equals(ACE) && card.suit().equals(HEARTS));

        cards.removeIf(card -> card.cardNumber().equals(JACK) && card.suit().equals(CLUBS));

        cards.removeIf(card -> card.cardNumber().equals(KING) && card.suit().equals(HEARTS));

        cards.add(0, new Card(ACE, CLUBS));
        cards.add(1, new Card(ACE, HEARTS));
        cards.add(2, new Card(JACK, CLUBS));
        cards.add(3, new Card(KING, HEARTS));
        return cards;
    }
}
