package cat.itacademy.blackjack.demo.game.application.service;

import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.game.application.service.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.demo.game.domain.GameState;
import cat.itacademy.blackjack.demo.game.domain.event.GameFinishedEvent;
import cat.itacademy.blackjack.demo.game.domain.event.GameFinishedEventPublisher;
import cat.itacademy.blackjack.demo.game.domain.exception.GameNotFoundException;
import cat.itacademy.blackjack.demo.game.domain.model.Dealer;
import cat.itacademy.blackjack.demo.game.domain.model.Deck;
import cat.itacademy.blackjack.demo.game.domain.model.Game;
import cat.itacademy.blackjack.demo.game.domain.model.UserPlayer;
import cat.itacademy.blackjack.demo.game.domain.value_object.Card;
import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.GameResponseDto;
import cat.itacademy.blackjack.demo.shuffle_strategy.GameWithoutBlackJackStrategy;
import cat.itacademy.blackjack.demo.shuffle_strategy.PlayerLosingByExceeding21Strategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class HitServiceTest {

    @Mock
    private GameFinishedEventPublisher eventPublisher;

    @Mock
    private ActiveGamePort gamePort;

    @Mock
    private ShuffleStrategy shuffleStrategy;

    @InjectMocks
    private HitService hitService;

    private final static String ID = UUID.randomUUID().toString();
    private final static GameId GAME_ID = GameId.fromString(ID);

        @Test
        @DisplayName("Should save game and return DTO when game starts normally and is not over")
        void shouldSaveGameAndReturnGameInfoWhenItIsNotOver() {
            String name = "Name";

            when(shuffleStrategy.shuffle(any())).thenAnswer(invocation -> {
                List<Card> cards = invocation.getArgument(0);
                GameWithoutBlackJackStrategy gameWithoutBlackJackStrategy = new GameWithoutBlackJackStrategy();
                gameWithoutBlackJackStrategy.shuffle(cards);
                return cards;
            });

            Game startedGame = Game.create(UserPlayer.create(Name.of(name)), Dealer.create(), Deck.create());
            startedGame.start(shuffleStrategy);

            LocalDateTime now = LocalDateTime.now();
            Game game = Game.reconstitute(GAME_ID, GameState.STARTED, startedGame.getUserPlayer(), startedGame.getDealer(), startedGame.getDeck(), now, now);

            when(gamePort.getActiveGame(GAME_ID)).thenReturn(Optional.of(game));

            when(gamePort.saveActiveGame(any(Game.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

            GameResponseDto result = hitService.execute(ID);

            assertThat(result).isNotNull();
            assertNotNull(result.id());
            assertTrue(result.lastTimePlayedAt().isBefore(LocalDateTime.now()));
            assertEquals(result.username(), name);
            assertTrue(result.playerTotalCardsValue() > 3);
            assertEquals(3, result.playerHand().size());
            assertNotNull(result.dealerFirstCard());
            assertNotNull(result.dealerTotalCardsValue());
            assertNotNull(result.gameState());
            assertNull(result.gameResult());
            assertNull(result.finishedWithBlackjack());

            verify(gamePort, times(1)).saveActiveGame(any(Game.class));
            verify(gamePort, never()).deleteActiveGame(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should publish event when game is OVER")
        void shouldPublishGameAndReturnInfoWhenItIsOver() {
            String name = "Name";
            when(shuffleStrategy.shuffle(any())).thenAnswer(invocation -> {
                List<Card> cards = invocation.getArgument(0);
                PlayerLosingByExceeding21Strategy playerLosingStrategy = new PlayerLosingByExceeding21Strategy();
                playerLosingStrategy.shuffle(cards);
                return cards;
            });

            Game startedGame = Game.create(UserPlayer.create(Name.of(name)), Dealer.create(), Deck.create());
            startedGame.start(shuffleStrategy);
            LocalDateTime now = LocalDateTime.now();
            Game gameReconstituted = Game.reconstitute(GAME_ID, GameState.STARTED, startedGame.getUserPlayer(), startedGame.getDealer(), startedGame.getDeck(), now, now);

            when(gamePort.getActiveGame(GAME_ID)).thenReturn(Optional.of(gameReconstituted));

            GameResponseDto result = hitService.execute(ID);

            assertThat(result).isNotNull();
            assertEquals(result.username(), name);
            assertThat(result.gameState()).isEqualTo(GameState.OVER.name());
            assertThat(result.gameResult()).isEqualTo(GameResult.DEALER_WIN.name());
            assertFalse(result.finishedWithBlackjack());
            assertThat(result.playerTotalCardsValue()).isGreaterThan(21);

            verify(gamePort, times(1)).deleteActiveGame(any(GameId.class));
            verify(eventPublisher, times(1)).publishEvent(any(GameFinishedEvent.class));
            verify(gamePort,never()).saveActiveGame(any());
        }

    @Test
    void shouldThrowGameNotFoundExceptionWhenPortReturnEmptyOptional () {

        when(gamePort.getActiveGame(any(GameId.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(GameNotFoundException.class, () -> {hitService.execute(ID);});

        assertTrue(exception.getMessage().contains("Game"));
        assertTrue(exception.getMessage().contains("not"));
        assertTrue(exception.getMessage().contains("found"));
        assertTrue(exception.getMessage().contains(ID));


        verify(gamePort, times(1)).getActiveGame(GAME_ID);
        verify(gamePort, never()).saveActiveGame(any(Game.class));
        verify(eventPublisher, never()).publishEvent(any());
    }
}
