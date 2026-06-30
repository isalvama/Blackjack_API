package cat.itacademy.blackjack.demo.active_game.domain.model;

import cat.itacademy.blackjack.demo.active_game.domain.CardNumber;
import cat.itacademy.blackjack.demo.active_game.domain.Suit;
import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidGameException;
import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidHandException;
import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    @Test
    void shouldCreateAnEmptyHandWithTotalValueAs0() {
        Hand hand = Hand.create();
        assertNotNull(hand);
        assertTrue(hand.getCards().isEmpty());
        assertEquals(0, (int) hand.getTotalValue());
    }

    @Test
    void reconstituteThrowsExceptionIfListIsNull() {
        Exception exception = assertThrows(InvalidHandException.class, () -> {
                    Hand.reconstitute(null, 0);
                }
        );
        assertTrue(exception.getMessage().contains("Invalid Hand"));
        assertTrue(exception.getMessage().contains("cards"));
        assertTrue(exception.getMessage().contains("null"));
    }

    @Test
    void reconstituteThrowsExceptionIfValueIsNull() {
        Exception exception = assertThrows(InvalidHandException.class, () -> {
                    Hand.reconstitute(List.of(), null);
                }
        );
        assertTrue(exception.getMessage().contains("Invalid Hand"));
        assertTrue(exception.getMessage().contains("value"));
        assertTrue(exception.getMessage().contains("null"));
    }

    @Test
    void shouldAddCardAndAddToTotalValue() {
        Hand hand = Hand.create();

        hand.addCard(new Card(CardNumber.KING, Suit.DIAMONDS));

        assertFalse(hand.getCards().isEmpty());
        assertEquals(1, hand.getCards().size());
        assertEquals(hand.getCards(), List.of(new Card(CardNumber.KING, Suit.DIAMONDS)));
        assertEquals(10, hand.getTotalValue());
    }


    @Test
    void addCardThrowsExceptionIfCardIsNull() {
        Hand hand = Hand.create();
        Exception exception = assertThrows(InvalidGameException.class, () -> {
                    hand.addCard(null);
                }
        );
        assertTrue(exception.getMessage().contains("Invalid Game"));
        assertTrue(exception.getMessage().contains("card"));
        assertTrue(exception.getMessage().contains("null"));
    }

    @Test
    void addCardThrowsExceptionIfCardIsRepeated() {
        Card repeatedCard = new Card(CardNumber.ACE, Suit.DIAMONDS);
        List<Card> cards = List.of(repeatedCard);
        Hand hand = Hand.reconstitute(cards, 1);
        Exception exception = assertThrows(InvalidGameException.class, () -> {
                    hand.addCard(repeatedCard);
                }
        );
        assertTrue(exception.getMessage().contains("Invalid Game"));
        assertTrue(exception.getMessage().contains("repeated card"));
    }

    @Test
    void addCardAddsCardinCardsAndIncrementsTotalValueByCardNumberValue() {
        Card card = new Card(CardNumber.SIX, Suit.DIAMONDS);
        Hand hand = Hand.create();

        hand.addCard(card);

        assertEquals(1, hand.getCards().size());
        assertEquals(card.cardNumber().getValue(), hand.getTotalValue());
    }

    @Test
    void addCardWhenBlackjackSetsTotalValueTo21() {
        Card queenCard = new Card(CardNumber.QUEEN, Suit.DIAMONDS);
        Card aceCard = new Card(CardNumber.ACE, Suit.DIAMONDS);

        Hand hand = Hand.create();

        hand.addCard(queenCard);
        hand.addCard(aceCard);

        assertEquals(2, hand.getCards().size());
        assertEquals(21, hand.getTotalValue());
    }

    @Test
    void addCardWhenBustsAceValueTurns1() {
        Card queenCard = new Card(CardNumber.THREE, Suit.DIAMONDS);
        Card aceCard = new Card(CardNumber.ACE, Suit.DIAMONDS);
        Card nineCard = new Card(CardNumber.NINE, Suit.DIAMONDS);
        Hand hand = Hand.create();

        hand.addCard(queenCard);
        hand.addCard(aceCard);

        assertEquals(2, hand.getCards().size());
        assertEquals(14, hand.getTotalValue());

        hand.addCard(nineCard);

        assertEquals(3, hand.getCards().size());
        assertEquals(13, hand.getTotalValue());
    }

    @Test
    void isBlackjackShouldReturnTrue() {
        Card aceCard = new Card(CardNumber.ACE, Suit.DIAMONDS);
        Card queenCard = new Card(CardNumber.QUEEN, Suit.SPADES);
        Card kingCard = new Card(CardNumber.KING, Suit.HEARTS);
        Card jackCard = new Card(CardNumber.JACK, Suit.CLUBS);

        List<Card> cards1 = List.of(aceCard, queenCard);
        Hand hand1 = Hand.reconstitute(cards1, 11);
        assertTrue(hand1.isBlackjack());

        List<Card> cards2 = List.of(aceCard, kingCard);
        Hand hand2 = Hand.reconstitute(cards2, 11);
        assertTrue(hand2.isBlackjack());

        List<Card> cards3 = List.of(aceCard, jackCard);
        Hand hand3 = Hand.reconstitute(cards3, 11);
        assertTrue(hand3.isBlackjack());
    }

    @Test
    void isBlackjackShouldReturnFalse() {
        Card aceCard = new Card(CardNumber.ACE, Suit.DIAMONDS);
        Card queenCard = new Card(CardNumber.QUEEN, Suit.SPADES);
        Card kingCard = new Card(CardNumber.KING, Suit.HEARTS);
        Card jackCard = new Card(CardNumber.JACK, Suit.CLUBS);
        Card eightCard = new Card(CardNumber.EIGHT, Suit.DIAMONDS);
        Card twoCard = new Card(CardNumber.TWO, Suit.SPADES);
        Card fiveCard = new Card(CardNumber.FIVE, Suit.HEARTS);
        Card nineCard = new Card(CardNumber.NINE, Suit.CLUBS);

        List<Card> cards1 = List.of(aceCard, fiveCard);
        Hand hand1 = Hand.reconstitute(cards1, 11);
        assertFalse(hand1.isBlackjack());

        List<Card> cards2 = List.of(kingCard, eightCard);
        Hand hand2 = Hand.reconstitute(cards2, 11);
        assertFalse(hand2.isBlackjack());

        List<Card> cards3 = List.of(queenCard, twoCard);
        Hand hand3 = Hand.reconstitute(cards3, 11);
        assertFalse(hand3.isBlackjack());

        List<Card> cards4 = List.of(jackCard, nineCard);
        Hand hand4 = Hand.reconstitute(cards4, 11);
        assertFalse(hand4.isBlackjack());

        List<Card> cards5 = List.of(aceCard, queenCard, fiveCard);
        Hand hand5 = Hand.reconstitute(cards5, 11);
        assertFalse(hand5.isBlackjack());
    }
}
