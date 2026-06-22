package cat.itacademy.blackjack.demo.game.application.port.in;

import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.GameResponseDto;

public interface HitUseCase {
    GameResponseDto execute (String id);
}
