package cat.itacademy.blackjack.demo.game.application.service;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.game.application.port.in.HitUseCase;
import cat.itacademy.blackjack.demo.game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.game.domain.GameState;
import cat.itacademy.blackjack.demo.game.domain.event.GameFinishedEvent;
import cat.itacademy.blackjack.demo.game.domain.event.GameFinishedEventPublisher;
import cat.itacademy.blackjack.demo.game.domain.exception.GameNotFoundException;
import cat.itacademy.blackjack.demo.game.domain.model.Game;
import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.GameResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HitService implements HitUseCase {
    private final ActiveGamePort gamePort;
    private final GameFinishedEventPublisher eventPublisher;


    @Override
    public GameResponseDto execute(String id) {
        Game game = gamePort.getGame(GameId.fromString(id)).orElseThrow(() -> new GameNotFoundException(id));
        game.hit();
        Game savedGame = gamePort.saveGame(game);
        game.updateAuditInfo(savedGame.getLastTimePlayedAt());

        if (game.getGameState() == GameState.OVER){
            eventPublisher.publishEvent(GameFinishedEvent.from(game)
            );
        }
        return GameResponseDto.from(game);
    }
}
