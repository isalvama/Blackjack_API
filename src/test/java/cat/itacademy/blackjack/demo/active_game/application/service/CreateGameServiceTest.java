package cat.itacademy.blackjack.demo.active_game.application.service;

import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.active_game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.active_game.domain.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.demo.active_game.domain.GameState;
import cat.itacademy.blackjack.demo.active_game.domain.event.GameFinishedEvent;
import cat.itacademy.blackjack.demo.active_game.domain.event.GameFinishedEventPublisher;
import cat.itacademy.blackjack.demo.active_game.domain.model.Game;
import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.ActiveGameResponseDto;
import cat.itacademy.blackjack.demo.shuffle_strategy.TieWithBlackjackShuffleStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGameServiceTest {

    private Game game;

    @Mock
    private GameFinishedEventPublisher eventPublisher;

    @Mock
    private ActiveGamePort gamePort;

    @Mock
    private ShuffleStrategy shuffleStrategy;

    @InjectMocks
    private CreateGameService createGameService;

    private final String PLAYER_NAME = "Name";

    @Nested
    class GameStartedWithoutBlackJack {
        @Test
        @DisplayName("Should save game and return DTO when game starts normally and is not over")
        void execute_Success() {

            when(gamePort.saveActiveGame(any(Game.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

            ActiveGameResponseDto result = createGameService.execute(PLAYER_NAME);

            assertThat(result).isNotNull();
            assertNotNull(result.id());
            assertTrue(result.createdAt().isBefore(LocalDateTime.now()));
            assertTrue(result.lastTimePlayedAt().isBefore(LocalDateTime.now()));
            assertEquals(result.username(), PLAYER_NAME);
            assertTrue(result.playerTotalCardsValue() > 1);
            assertEquals(2, result.playerHand().size());
            assertNotNull(result.dealerFirstCard());
            assertEquals(GameState.STARTED.name(), result.gameState());
            assertNull(result.gameResult());
            assertNull(result.finishedWithBlackjack());

            verify(gamePort, times(1)).saveActiveGame(any(Game.class));
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    class GameFinishedUserWinningWithBlackJack {
        @Test
        @DisplayName("Should publish event when game is OVER (immediate Blackjack)")
        void execute_GameOverImmediate() {
            when(shuffleStrategy.shuffle(any())).thenAnswer(invocation -> {
                List<Card> cards = invocation.getArgument(0);
                TieWithBlackjackShuffleStrategy tieWithBlackjackShuffleStrategy = new TieWithBlackjackShuffleStrategy();
                tieWithBlackjackShuffleStrategy.shuffle(cards);
                return cards;
            });

            ActiveGameResponseDto result = createGameService.execute(PLAYER_NAME);

            assertThat(result).isNotNull();
            assertNotNull(result.id());
            assertTrue(result.createdAt().isBefore(LocalDateTime.now()));
            assertTrue(result.lastTimePlayedAt().isBefore(LocalDateTime.now()));
            assertEquals(result.username(), PLAYER_NAME);
            assertEquals(21, result.playerTotalCardsValue());
            assertEquals(2, result.playerHand().size());
            assertNotNull(result.dealerFirstCard());
            assertEquals(GameState.OVER.name(), result.gameState());
            assertEquals(GameResult.TIE.name(), result.gameResult());
            assertTrue(result.finishedWithBlackjack());


            verify(gamePort, never()).saveActiveGame(any(Game.class));
            verify(eventPublisher, times(1)).publishEvent(any(GameFinishedEvent.class));
        }
    }
}