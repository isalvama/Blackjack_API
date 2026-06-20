package cat.itacademy.blackjack.demo.game.domain.model;

import cat.itacademy.blackjack.demo.game.domain.CardNumber;
import cat.itacademy.blackjack.demo.game.domain.Suit;
import cat.itacademy.blackjack.demo.game.domain.value_object.Card;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void shouldCreateANewDeck() {
        Deck deck = Deck.create();
        assertNotNull(deck);
        assertFalse(deck.cards.isEmpty());
        int expectedSize = Suit.values().length * CardNumber.values().length;
        assertEquals(deck.cards.size(), expectedSize);
        assertThat(deck.getCards()).containsOnlyOnceElementsOf(deck.getCards());
        assertThat(deck.getCards())
                .extracting(Card::suit)
                .containsAll(java.util.Arrays.asList(Suit.values()));
        assertThat(deck.getCards())
                .extracting(Card::cardNumber)
                .containsAll(java.util.Arrays.asList(CardNumber.values()));
        assertThat(deck.getCards())
                .anyMatch(card -> card.cardNumber() == CardNumber.ACE && card.suit() == Suit.SPADES);
    }

    @Test
    void create() {
    }

    @Test
    void shouldCreateANewDeckFromListOfCards() {
        Card card = new Card(CardNumber.ACE, Suit.CLUBS);
        List<Card> cards = List.of(card);
        Deck deck = Deck.from(cards);
        assertNotNull(deck);
        assertTrue(deck.cards.contains(card));
    }

    @Test
    void hasNoCardsShouldReturnFalseWhenNotEmpty() {
        Deck deck = Deck.create();
        assertThat(deck.hasNoCards()).isFalse();
    }

    @Test
    void drawShouldReturnAndRemoveFirstCard() {
        Deck deck = Deck.create();
        int initialSize = deck.getCards().size();
        Card firstCard = deck.getCards().getFirst();

        Card drawnCard = deck.draw();

        assertThat(drawnCard).isEqualTo(firstCard);
        assertThat(deck.getCards()).hasSize(initialSize - 1);
        assertThat(deck.getCards().getFirst()).isNotEqualTo(firstCard);
    }
}