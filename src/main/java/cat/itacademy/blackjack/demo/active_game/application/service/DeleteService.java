package cat.itacademy.blackjack.demo.active_game.application.service;

import cat.itacademy.blackjack.demo.active_game.application.exception.ActiveGameNotFoundException;
import cat.itacademy.blackjack.demo.active_game.application.port.in.DeleteUseCase;
import cat.itacademy.blackjack.demo.active_game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteService implements DeleteUseCase {
    private final ActiveGamePort gamePort;

    @Override
    public void execute(String id) {
        gamePort.getActiveGame(GameId.fromString(id)).orElseThrow(() -> new ActiveGameNotFoundException(id));
        gamePort.deleteActiveGame(GameId.fromString(id));
    }
}
