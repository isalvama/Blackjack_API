package cat.itacademy.blackjack.demo.active_game.application.port.in;

import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.ActiveGameResponseDto;

public interface CreateGameUseCase {
    ActiveGameResponseDto execute (String playerName);
}
