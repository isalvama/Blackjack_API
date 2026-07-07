package cat.itacademy.blackjack.demo.active_game.domain.model;

import cat.itacademy.blackjack.demo.active_game.domain.exception.InvalidHitException;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.shuffle_strategy.GameWithoutBlackJackStrategy;
import cat.itacademy.blackjack.demo.shuffle_strategy.TieWithBlackjackShuffleStrategy;
import cat.itacademy.blackjack.demo.shuffle_strategy.TieWithoutBlackJackStrategy;
import cat.itacademy.blackjack.demo.shuffle_strategy.UserWinningWithBlackjackShuffleStrategy;
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

    private Game mockedGame;
    private static final String NAME = "Player Name";

    @BeforeEach
    void setUp() {
        when(deck.getCards()).thenReturn(List.of(card));
        mockedGame = Game.create(userPlayer, dealer, deck);
    }

    @Nested
    class Start {

        Game gameWithMockedPlayers;
        Game gameWithoutMocks;

        @BeforeEach
        void setUp(){
            gameWithMockedPlayers = Game.create(userPlayer, dealer, Deck.create());
            gameWithoutMocks = Game.create(UserPlayer.create(Name.of(NAME)), Dealer.create(), Deck.create());
        }

        @Test
        void shouldDistributeInitialCards() {
            GameWithoutBlackJackStrategy gameWithoutBlackJackStrategy = new GameWithoutBlackJackStrategy();
            gameWithMockedPlayers.start(gameWithoutBlackJackStrategy);

            verify(dealer).hit(any(Card.class));
            verify(userPlayer, times(2)).hit(any(Card.class));
            assertThat(mockedGame.getGameState()).isEqualTo(GameState.STARTED);
        }

        @Test
        void shouldFinishIfPlayerHasBlackjackOnStart() {
            ShuffleStrategy userWinningWithBlackjackShuffleStrategy = new UserWinningWithBlackjackShuffleStrategy();
            gameWithoutMocks.start(userWinningWithBlackjackShuffleStrategy);

            assertThat(gameWithoutMocks.getGameState()).isEqualTo(GameState.OVER);
            assertThat(gameWithoutMocks.getGameOutcome().result()).isEqualTo(GameResult.USER_WIN);
            assertThat(gameWithoutMocks.getGameOutcome().blackjack()).isTrue();
        }

        @Test
        void shouldFinishWithTieBlackjackOnStart() {
            ShuffleStrategy tieWithBlackjackStrategy = new TieWithBlackjackShuffleStrategy();
            gameWithoutMocks.start(tieWithBlackjackStrategy);

            assertThat(gameWithoutMocks.getGameState()).isEqualTo(GameState.OVER);
            assertThat(gameWithoutMocks.getGameOutcome().result()).isEqualTo(GameResult.TIE);
            assertThat(gameWithoutMocks.getGameOutcome().blackjack()).isTrue();
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
    class PlayerRequestedHit {

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

            mockedGame.playerRequestedHit();

            verify(userPlayer).hit(card);
            assertThat(mockedGame.getGameState()).isEqualTo(GameState.STARTED);
        }

        @Test
        void shouldFinishGameIfPlayerBusts() {
            when(deck.hasNoCards()).thenReturn(false);
            when(deck.draw()).thenReturn(card);
            when(userPlayer.totalValueIsGreaterThan21()).thenReturn(true);

            mockedGame.playerRequestedHit();

            assertThat(mockedGame.getGameState()).isEqualTo(GameState.OVER);
            assertThat(mockedGame.getGameOutcome().result()).isEqualTo(GameResult.DEALER_WIN);
        }

        @Test
        void shouldFinishGameIfDeckIsEmptyOnHit() {
            when(deck.hasNoCards()).thenReturn(true);
            when(userPlayer.getHandValue()).thenReturn(20);
            when(dealer.getHandValue()).thenReturn(18);

            mockedGame.playerRequestedHit();

            assertThat(mockedGame.getGameState()).isEqualTo(GameState.OVER);
            assertThat(mockedGame.getGameOutcome().result()).isEqualTo(GameResult.USER_WIN);
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

            mockedGame.stand();

            assertThat(mockedGame.getGameState()).isEqualTo(GameState.OVER);
            assertThat(mockedGame.getGameOutcome().result()).isEqualTo(GameResult.USER_WIN);
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

            mockedGame.stand();

            verify(dealer).hit(card);
            assertThat(mockedGame.getGameState()).isEqualTo(GameState.OVER);
            assertThat(mockedGame.getGameOutcome().result()).isEqualTo(GameResult.USER_WIN);
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

            mockedGame.stand();

            verify(dealer).hit(card);
            assertThat(mockedGame.getGameState()).isEqualTo(GameState.OVER);
            assertThat(mockedGame.getGameOutcome().result()).isEqualTo(GameResult.DEALER_WIN);
        }

        @Test
        void shouldDealerWinWithBlackjack() {
            when(dealer.shouldStand()).thenReturn(true);
            when(dealer.checkBlackjack()).thenReturn(true);

            mockedGame.stand();

            assertThat(mockedGame.getGameOutcome().result()).isEqualTo(GameResult.DEALER_WIN);
            assertThat(mockedGame.getGameOutcome().blackjack()).isTrue();
            assertThat(mockedGame.getGameState()).isEqualTo(GameState.OVER);
        }

        @Test
        void shouldUserWinIfDealerBusts() {
            when(dealer.shouldStand()).thenReturn(true);
            when(dealer.checkBlackjack()).thenReturn(false);
            when(dealer.totalValueIsGreaterThan21()).thenReturn(true);

            mockedGame.stand();

            assertThat(mockedGame.getGameOutcome().result()).isEqualTo(GameResult.USER_WIN);
            Assertions.assertFalse(mockedGame.getGameOutcome().blackjack());
            assertThat(mockedGame.getGameState()).isEqualTo(GameState.OVER);
        }
    }
}