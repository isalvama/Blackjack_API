package cat.itacademy.blackjack.game.application.port.in;

import cat.itacademy.blackjack.game.infrastructure.web.dto.GameResponseDto;

public interface CreateGameUseCase {
    GameResponseDto execute (String playerName);
}
