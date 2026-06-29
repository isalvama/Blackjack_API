package cat.itacademy.blackjack.demo.active_game.application.port.in;

import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.GameResponseDto;

public interface CreateGameUseCase {
    GameResponseDto execute (String playerName);
}
