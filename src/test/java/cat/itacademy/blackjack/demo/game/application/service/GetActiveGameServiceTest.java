package cat.itacademy.blackjack.demo.game.application.service;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.game.domain.GameState;
import cat.itacademy.blackjack.demo.game.domain.exception.GameNotFoundException;
import cat.itacademy.blackjack.demo.game.domain.model.Dealer;
import cat.itacademy.blackjack.demo.game.domain.model.Deck;
import cat.itacademy.blackjack.demo.game.domain.model.Game;
import cat.itacademy.blackjack.demo.game.domain.model.UserPlayer;
import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.GameResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetActiveGameServiceTest {

    private final static String ID = UUID.randomUUID().toString();
    private final static GameId GAME_ID = GameId.fromString(ID);

    @Mock
    private ActiveGamePort activeGamePort;

    @InjectMocks
    private GetActiveGameService getActiveGameService;

    @Test
    void shouldCallGetGameSuccessfullyAndReturnAGameResponseDtoWithInfo () {
        String name = "Name";
        UserPlayer userPlayer = UserPlayer.create(Name.of(name));
        Game game = Game.reconstitute(GAME_ID, GameState.STARTED, userPlayer, Dealer.create(), Deck.create(), LocalDateTime.now(), LocalDateTime.now());
        game.dealerHits();
        game.hit();
        game.hit();

        when(activeGamePort.getActiveGame(GAME_ID)).thenReturn(Optional.of(game));

        GameResponseDto gameResponseDto = getActiveGameService.execute(ID);

        assertEquals(gameResponseDto.id(), ID);
        assertNotNull(gameResponseDto.createdAt());
        assertNotNull(gameResponseDto.lastTimePlayedAt());
        assertEquals(gameResponseDto.username(), name);
        assertNotNull(gameResponseDto.playerTotalCardsValue());
        assertNotNull(gameResponseDto.playerHand());
        assertNotNull(gameResponseDto.dealerFirstCard());
        assertEquals(gameResponseDto.gameState(), GameState.STARTED.name());
        assertNull(gameResponseDto.gameResult());
        assertNull(gameResponseDto.finishedWithBlackjack());

        verify(activeGamePort, times(1)).getActiveGame(GAME_ID);
    }

    @Test
    void shouldThrowGameNotFoundExceptionWhenPortReturnEmptyOptional () {

        when(activeGamePort.getActiveGame(GAME_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(GameNotFoundException.class, () -> {getActiveGameService.execute(ID);});

        assertTrue(exception.getMessage().contains("Game"));
        assertTrue(exception.getMessage().contains("not"));
        assertTrue(exception.getMessage().contains("found"));
        assertTrue(exception.getMessage().contains(ID));


        verify(activeGamePort, times(1)).getActiveGame(GAME_ID);
    }
}