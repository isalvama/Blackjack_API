package cat.itacademy.blackjack.demo.active_game.application.service;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.active_game.application.port.in.GetActiveGameUseCase;
import cat.itacademy.blackjack.demo.active_game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.active_game.domain.exception.GameNotFoundException;
import cat.itacademy.blackjack.demo.active_game.domain.model.Game;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.ActiveGameResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetActiveGameService implements GetActiveGameUseCase {
    private final ActiveGamePort gamePort;

    @Override
    public ActiveGameResponseDto execute(String id) {
        Game game = gamePort.getActiveGame(GameId.fromString(id)).orElseThrow(() -> new GameNotFoundException(id));
        return ActiveGameResponseDto.from(game);
    }
}
