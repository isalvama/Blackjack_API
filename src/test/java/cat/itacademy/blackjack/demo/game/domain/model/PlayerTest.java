package cat.itacademy.blackjack.demo.game.domain.model;

import cat.itacademy.blackjack.demo.game.domain.CardNumber;
import cat.itacademy.blackjack.demo.game.domain.Suit;
import cat.itacademy.blackjack.demo.game.domain.exception.InvalidGameException;
import cat.itacademy.blackjack.demo.game.domain.exception.InvalidPlayerException;
import cat.itacademy.blackjack.demo.game.domain.value_object.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PlayerTest {

    static class TestPlayer extends Player {
        TestPlayer(Hand hand) { super(hand); }
    }

    @Test
    void constructorShouldThrowInvalidPlayerExceptionWhenNullHand() {
        assertThatThrownBy(() -> new TestPlayer(null))
                .isInstanceOf(InvalidPlayerException.class);
    }

    @Test
    void shouldThrowInvalidGameExceptionWhenHitNullCard() {
        Player player = new TestPlayer(Hand.reconstitute(new ArrayList<>(), 0));
        assertThatThrownBy(() -> player.hit(null))
                .isInstanceOf(InvalidGameException.class);
    }

    @Test
    void shouldAddCardToHandWhenHitValidCard() {
        Hand mockHand = mock(Hand.class);
        Card card = new Card(CardNumber.ACE, Suit.CLUBS);
        Player player = new TestPlayer(mockHand);

        player.hit(card);

        verify(mockHand).addCard(card);
    }
}