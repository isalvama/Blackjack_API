package cat.itacademy.blackjack.demo.active_game.domain.model;

import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.active_game.domain.CardNumber;
import cat.itacademy.blackjack.demo.active_game.domain.Suit;
import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidPlayerException;
import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

class DealerTest {
    private final static Name NAME = Name.of("Name");
    private final static Dealer DEALER = Dealer.create();


    @Test
    void shouldCreateDealerWithEmptyHand() {
        assertThat(DEALER).isNotNull();
        assertThat(DEALER.getHand().getCards()).isEmpty();
    }

    @Test
    void shouldNotStandBelow17() {
        DEALER.hit(new Card(CardNumber.TEN, Suit.CLUBS)); // 10
        DEALER.hit(new Card(CardNumber.SIX, Suit.CLUBS)); // 16

        assertThat(DEALER.shouldStand()).isFalse();
    }

    @Test
    void shouldStandAt17() {
        DEALER.hit(new Card(CardNumber.TEN, Suit.CLUBS)); // 10
        DEALER.hit(new Card(CardNumber.SEVEN, Suit.CLUBS)); // 17

        assertThat(DEALER.shouldStand()).isTrue();
    }

    @Test
    void shouldStandWithBlackjack() {
        DEALER.hit(new Card(CardNumber.ACE, Suit.CLUBS));
        DEALER.hit(new Card(CardNumber.KING, Suit.CLUBS));

        assertThat(DEALER.shouldStand()).isTrue();
    }

    @Test
    void shouldThrowInvalidPlayerExceptionWhenHandIsNull() {
        Exception exception = assertThrows(InvalidPlayerException.class, () -> UserPlayer.reconstitute(NAME, null));
        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void shouldThrowInvalidPlayerExceptionWhenHandIsEmpty() {
        Hand emptyHand = Hand.create();

        Exception exception = assertThrows(InvalidPlayerException.class,() -> UserPlayer.reconstitute(NAME, emptyHand));
        assertTrue(exception.getMessage().contains("hand cannot be empty"));
    }
}