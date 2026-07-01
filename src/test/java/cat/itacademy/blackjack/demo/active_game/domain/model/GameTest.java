package cat.itacademy.blackjack.demo.active_game.domain.model;

import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidHitException;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import org.junit.jupiter.api.*;

import cat.itacademy.blackjack.demo.active_game.domain.GameState;
import cat.itacademy.blackjack.demo.active_game.domain.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;
import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.active_game.domain.exception.ActiveGameException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameTest {

    @Mock
    private UserPlayer userPlayer;
    @Mock
    private Dealer dealer;
    @Mock
    private Deck deck;
    @Mock
    private ShuffleStrategy shuffleStrategy;
    @Mock
    private Card card;

    private Game game;

    @BeforeEach
    void setUp() {
        when(deck.getCards()).thenReturn(List.of(card));
        game = Game.create(userPlayer, dealer, deck);
    }

    @Nested
    class Start {

        @Test
        void shouldShuffleAndDistributeInitialCards() {
            when(deck.draw()).thenReturn(card);
            when(userPlayer.checkBlackjack()).thenReturn(false);

            game.start(shuffleStrategy);

            verify(deck).shuffle(shuffleStrategy);
            verify(deck, times(3)).draw();
            verify(dealer).hit(card);
            verify(userPlayer, times(2)).hit(card);
            assertThat(game.getGameState()).isEqualTo(GameState.STARTED);
        }

        @Test
        void shouldFinishIfPlayerHasBlackjackOnStart() {
            when(deck.draw()).thenReturn(card);
            when(userPlayer.checkBlackjack()).thenReturn(true);
            when(dealer.shouldStand()).thenReturn(true);
            when(dealer.checkBlackjack()).thenReturn(false);

            game.start(shuffleStrategy);

            assertThat(game.getGameState()).isEqualTo(GameState.OVER);
            assertThat(game.getGameOutcome().result()).isEqualTo(GameResult.USER_WIN);
            assertThat(game.getGameOutcome().blackjack()).isTrue();
        }

        @Test
        void shouldFinishWithTieBlackjackOnStart() {
            when(deck.draw()).thenReturn(card);
            when(userPlayer.checkBlackjack()).thenReturn(true);
            when(dealer.shouldStand()).thenReturn(true);
            when(dealer.checkBlackjack()).thenReturn(true);

            game.start(shuffleStrategy);

            assertThat(game.getGameState()).isEqualTo(GameState.OVER);
            assertThat(game.getGameOutcome().result()).isEqualTo(GameResult.TIE);
            assertThat(game.getGameOutcome().blackjack()).isTrue();
        }

        @Test
        void shouldThrowExceptionIfAlreadyStarted() {
            Game overGame = Game.reconstitute(GameId.fromUUID(UUID.randomUUID()), GameState.OVER, userPlayer, dealer, deck, LocalDateTime.now(), LocalDateTime.now());

            assertThatThrownBy(() -> overGame.start(shuffleStrategy))
                    .isInstanceOf(ActiveGameException.class)
                    .hasMessageContaining("already started");
        }
    }

    @Nested
    class playerRequestedHit {

        @Test
        void shouldTrowInvalidHitExceptionWhenGameIsOver() {
            Game overGame = Game.reconstitute(GameId.fromUUID(UUID.randomUUID()), GameState.OVER, userPlayer, dealer, deck, LocalDateTime.now(), LocalDateTime.now());
            Assertions.assertThrows(InvalidHitException.class, overGame::playerRequestedHit);
            verify(userPlayer, never()).hit(card);
        }

        @Test
        void shouldAllowHitWhenGameIsActiveDeckHasCardsAndValueIsUnder21() {
            when(deck.hasNoCards()).thenReturn(false);
            when(deck.draw()).thenReturn(card);
            when(userPlayer.totalValueIsGreaterThan21()).thenReturn(false);

            game.playerRequestedHit();

            verify(userPlayer).hit(card);
            assertThat(game.getGameState()).isEqualTo(GameState.STARTED);
        }

        @Test
        void shouldFinishGameIfPlayerBusts() {
            when(deck.hasNoCards()).thenReturn(false);
            when(deck.draw()).thenReturn(card);
            when(userPlayer.totalValueIsGreaterThan21()).thenReturn(true);

            game.playerRequestedHit();

            assertThat(game.getGameState()).isEqualTo(GameState.OVER);
            assertThat(game.getGameOutcome().result()).isEqualTo(GameResult.DEALER_WIN);
        }

        @Test
        void shouldFinishGameIfDeckIsEmptyOnHit() {
            when(deck.hasNoCards()).thenReturn(true);
            when(userPlayer.getHandValue()).thenReturn(20);
            when(dealer.getHandValue()).thenReturn(18);

            game.playerRequestedHit();

            assertThat(game.getGameState()).isEqualTo(GameState.OVER);
            assertThat(game.getGameOutcome().result()).isEqualTo(GameResult.USER_WIN);
            verify(userPlayer, never()).hit(any());
        }
    }

    @Nested
    class Stand {

        @Test
        void shouldTrowInvalidHitExceptionWhenGameIsOver() {
            Game overGame = Game.reconstitute(GameId.fromUUID(UUID.randomUUID()), GameState.OVER, userPlayer, dealer, deck, LocalDateTime.now(), LocalDateTime.now());
            Assertions.assertThrows(InvalidHitException.class, overGame::stand);
            verify(dealer, never()).hit(card);
        }

        @Test
        void shouldFinishGameIfDeckIsEmptyOnHit() {
            when(deck.hasNoCards()).thenReturn(true);
            when(userPlayer.getHandValue()).thenReturn(20);
            when(dealer.getHandValue()).thenReturn(18);

            game.stand();

            assertThat(game.getGameState()).isEqualTo(GameState.OVER);
            assertThat(game.getGameOutcome().result()).isEqualTo(GameResult.USER_WIN);
            verify(dealer, never()).hit(any());
        }

        @Test
        void shouldWinPlayerByPointsAfterStand() {
            when(dealer.shouldStand()).thenReturn(false, true);
            when(deck.hasNoCards()).thenReturn(false);
            when(deck.draw()).thenReturn(card);

            when(dealer.checkBlackjack()).thenReturn(false);
            when(dealer.totalValueIsGreaterThan21()).thenReturn(false);
            when(userPlayer.getHandValue()).thenReturn(20);
            when(dealer.getHandValue()).thenReturn(19);

            game.stand();

            verify(dealer).hit(card);
            assertThat(game.getGameState()).isEqualTo(GameState.OVER);
            assertThat(game.getGameOutcome().result()).isEqualTo(GameResult.USER_WIN);
        }

        @Test
        void shouldWinDealerByPointsAfterStand() {
            when(dealer.shouldStand()).thenReturn(false, true);
            when(deck.hasNoCards()).thenReturn(false);
            when(deck.draw()).thenReturn(card);

            when(dealer.checkBlackjack()).thenReturn(false);
            when(dealer.totalValueIsGreaterThan21()).thenReturn(false);
            when(userPlayer.getHandValue()).thenReturn(13);
            when(dealer.getHandValue()).thenReturn(19);

            game.stand();

            verify(dealer).hit(card);
            assertThat(game.getGameState()).isEqualTo(GameState.OVER);
            assertThat(game.getGameOutcome().result()).isEqualTo(GameResult.DEALER_WIN);
        }

        @Test
        void shouldDealerWinWithBlackjack() {
            when(dealer.shouldStand()).thenReturn(true);
            when(dealer.checkBlackjack()).thenReturn(true);

            game.stand();

            assertThat(game.getGameOutcome().result()).isEqualTo(GameResult.DEALER_WIN);
            assertThat(game.getGameOutcome().blackjack()).isTrue();
            assertThat(game.getGameState()).isEqualTo(GameState.OVER);
        }

        @Test
        void shouldUserWinIfDealerBusts() {
            when(dealer.shouldStand()).thenReturn(true);
            when(dealer.checkBlackjack()).thenReturn(false);
            when(dealer.totalValueIsGreaterThan21()).thenReturn(true);

            game.stand();

            assertThat(game.getGameOutcome().result()).isEqualTo(GameResult.USER_WIN);
            Assertions.assertFalse(game.getGameOutcome().blackjack());
            assertThat(game.getGameState()).isEqualTo(GameState.OVER);
        }
    }
}