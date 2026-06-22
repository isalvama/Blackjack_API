package cat.itacademy.blackjack.demo.active_game.application.service;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.active_game.application.port.in.HitUseCase;
import cat.itacademy.blackjack.demo.active_game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.active_game.domain.GameState;
import cat.itacademy.blackjack.demo.active_game.domain.event.GameFinishedEvent;
import cat.itacademy.blackjack.demo.active_game.domain.event.GameFinishedEventPublisher;
import cat.itacademy.blackjack.demo.active_game.domain.exception.GameNotFoundException;
import cat.itacademy.blackjack.demo.active_game.domain.model.Game;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.GameResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HitService implements HitUseCase {
    private final ActiveGamePort gamePort;
    private final GameFinishedEventPublisher eventPublisher;


    @Override
    public GameResponseDto execute(String id) {
        Game game = gamePort.getActiveGame(GameId.fromString(id)).orElseThrow(() -> new GameNotFoundException(id));
        game.hit();
        game.updateAuditInfo(LocalDateTime.now());

        if (game.getGameState() == GameState.OVER){
            gamePort.deleteActiveGame(game.getId());
            eventPublisher.publishEvent(GameFinishedEvent.from(game));
        } else {
            gamePort.saveActiveGame(game);
        }
        return GameResponseDto.from(game);
    }
}
