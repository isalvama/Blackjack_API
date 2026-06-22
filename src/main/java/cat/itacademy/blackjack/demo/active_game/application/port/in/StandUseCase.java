package cat.itacademy.blackjack.demo.active_game.application.port.in;

import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.GameResponseDto;

public interface StandUseCase {
    GameResponseDto execute (String id);
}
