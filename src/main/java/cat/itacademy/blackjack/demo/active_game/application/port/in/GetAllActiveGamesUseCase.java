package cat.itacademy.blackjack.demo.active_game.application.port.in;

import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.ActiveGameResponseDto;

import java.util.List;

public interface GetAllActiveGamesUseCase {
    List<ActiveGameResponseDto> execute();
}
