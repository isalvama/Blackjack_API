package cat.itacademy.blackjack.demo.game.application.service;

import cat.itacademy.blackjack.demo.game.application.port.in.GetActiveGameUseCase;
import cat.itacademy.blackjack.demo.game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.GameResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetActiveGameService implements GetActiveGameUseCase {
    private final ActiveGamePort gamePort;

    @Override
    public GameResponseDto execute(String id) {

        return null;
    }
}
