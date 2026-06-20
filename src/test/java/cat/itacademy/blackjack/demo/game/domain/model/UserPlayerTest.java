package cat.itacademy.blackjack.demo.game.domain.model;

import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.game.domain.CardNumber;
import cat.itacademy.blackjack.demo.game.domain.Suit;
import cat.itacademy.blackjack.demo.game.domain.exception.InvalidPlayerException;
import cat.itacademy.blackjack.demo.game.domain.value_object.Card;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

class UserPlayerTest {
    private final static Name NAME = Name.of("Name");

    @Test
    void shouldCreateWithFactoryMethod() {

        UserPlayer player = UserPlayer.create(NAME);

        assertThat(player.getName()).isEqualTo(NAME);
        assertThat(player.getHand().getCards()).isEmpty();
    }


    @Test
    void shouldReconstituteValidUserWhenNameAndHandAreValid() {
        Hand hand = Hand.create();
        hand.addCard(new Card(CardNumber.ACE, Suit.CLUBS));

        UserPlayer player = UserPlayer.reconstitute(NAME, hand);

        assertThat(player.getName()).isEqualTo(NAME);
        assertThat(player.getHand()).isEqualTo(hand);
        assertThat(player.getHand().getCards()).isNotEmpty();
    }

    @Test
    void shouldThrowInvalidPlayerExceptionWhenNameIsNull() {
       assertThrows(InvalidPlayerException.class, () -> UserPlayer.create(null));
    }

    @Test
    void shouldReconstituteWhenDataIsValid() {
        Hand hand = Hand.create();
        hand.addCard(new Card(CardNumber.KING, Suit.DIAMONDS));
        Assertions.assertDoesNotThrow(() -> UserPlayer.reconstitute(NAME, hand));
    }

    @Test
    void shouldThrowInvalidPlayerExceptionWhenHandIsNull() {
        Exception exception = assertThrows(InvalidPlayerException.class, () -> UserPlayer.reconstitute(NAME, null));
        assertTrue(exception.getMessage().contains("null"));
    }

    @Test
    void shouldThrowInvalidPlayerExceptionWhenHandIsEmpty() {
        Hand emptyHand = Hand.create();

        Exception exception = assertThrows(InvalidPlayerException.class,() -> UserPlayer.reconstitute(NAME, emptyHand));
        assertTrue(exception.getMessage().contains("hand cannot be empty"));
    }
}