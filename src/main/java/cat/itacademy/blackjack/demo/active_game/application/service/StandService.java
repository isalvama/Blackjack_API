package cat.itacademy.blackjack.demo.active_game.application.service;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.active_game.application.port.in.StandUseCase;
import cat.itacademy.blackjack.demo.active_game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.active_game.domain.event.GameFinishedEvent;
import cat.itacademy.blackjack.demo.active_game.domain.event.GameFinishedEventPublisher;
import cat.itacademy.blackjack.demo.active_game.application.exception.ActiveGameNotFoundException;
import cat.itacademy.blackjack.demo.active_game.domain.model.Game;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.ActiveGameResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StandService implements StandUseCase {
    private final ActiveGamePort gamePort;
    private final GameFinishedEventPublisher eventPublisher;

    @Override
    public ActiveGameResponseDto execute(String id) {
        Game game = gamePort.getActiveGame(GameId.fromString(id)).orElseThrow(() -> new ActiveGameNotFoundException(id));

        game.stand();
        game.updateAuditInfo(LocalDateTime.now());

        gamePort.deleteActiveGame(game.getId());
        eventPublisher.publishEvent(GameFinishedEvent.from(game));

        return ActiveGameResponseDto.from(game);
    }
}
