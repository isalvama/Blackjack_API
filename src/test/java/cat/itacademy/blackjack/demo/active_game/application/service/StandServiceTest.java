package cat.itacademy.blackjack.demo.active_game.application.service;

import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.active_game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.active_game.domain.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.demo.active_game.domain.GameState;
import cat.itacademy.blackjack.demo.active_game.domain.event.GameFinishedEvent;
import cat.itacademy.blackjack.demo.active_game.domain.event.GameFinishedEventPublisher;
import cat.itacademy.blackjack.demo.active_game.domain.exception.GameNotFoundException;
import cat.itacademy.blackjack.demo.active_game.domain.model.Dealer;
import cat.itacademy.blackjack.demo.active_game.domain.model.Deck;
import cat.itacademy.blackjack.demo.active_game.domain.model.Game;
import cat.itacademy.blackjack.demo.active_game.domain.model.UserPlayer;
import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.GameResponseDto;
import cat.itacademy.blackjack.demo.shuffle_strategy.GameWithoutBlackJackStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StandServiceTest {

    @Mock
    private GameFinishedEventPublisher eventPublisher;

    @Mock
    private ActiveGamePort gamePort;

    @Mock
    private ShuffleStrategy shuffleStrategy;

    @InjectMocks
    private StandService standService;

    private final static String ID = UUID.randomUUID().toString();
    private final static GameId GAME_ID = GameId.fromString(ID);


    @Test
    @DisplayName("Should publish event when game is OVER")
    void shouldPublishGameAndReturnInfoWhenItIsOver() {
        String name = "Name";
        LocalDateTime now = LocalDateTime.now();
        when(shuffleStrategy.shuffle(any())).thenAnswer(invocation -> {
            List<Card> cards = invocation.getArgument(0);
            GameWithoutBlackJackStrategy gameWithoutBlackJackStrategy = new GameWithoutBlackJackStrategy();
            gameWithoutBlackJackStrategy.shuffle(cards);
            return cards;
        });

        Game startedGame = Game.create(UserPlayer.create(Name.of(name)), Dealer.create(), Deck.create());
        startedGame.start(shuffleStrategy);

        Game game = Game.reconstitute(startedGame.getId(), startedGame.getGameState(), startedGame.getUserPlayer(), startedGame.getDealer(), startedGame.getDeck(), now, now);

        when(gamePort.getActiveGame(GAME_ID)).thenReturn(Optional.of(game));

        GameResponseDto result = standService.execute(ID);

        assertThat(result).isNotNull();
        assertThat(result.gameState()).isEqualTo(GameState.OVER.name());
        assertThat(result.gameResult()).isEqualTo(GameResult.USER_WIN.name());
        assertFalse(result.finishedWithBlackjack());
        assertThat(result.dealerTotalCardsValue()).isGreaterThan(2);
        assertThat(result.dealerFinalHand()).isNotNull();
        assertThat(result.dealerFinalHand().size()).isGreaterThan(1);

        verify(gamePort, times(1)).getActiveGame(GAME_ID);
        verify(gamePort, times(1)).deleteActiveGame(any(GameId.class));
        verify(eventPublisher, times(1)).publishEvent(any(GameFinishedEvent.class));
    }

    @Test
    void shouldThrowGameNotFoundExceptionWhenPortReturnEmptyOptional () {

        when(gamePort.getActiveGame(any(GameId.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(GameNotFoundException.class, () -> {standService.execute(ID);});

        assertTrue(exception.getMessage().contains("Game"));
        assertTrue(exception.getMessage().contains("not"));
        assertTrue(exception.getMessage().contains("found"));
        assertTrue(exception.getMessage().contains(ID));


        verify(gamePort, times(1)).getActiveGame(GAME_ID);
        verify(gamePort, never()).deleteActiveGame(any(GameId.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

}