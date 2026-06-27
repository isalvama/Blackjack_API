package cat.itacademy.blackjack.demo.finished_game.application.port.in;

import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.FinishedGameResponseDto;

public interface GetFinishedGameUseCase {
    FinishedGameResponseDto getGameById(String gameId);
}
