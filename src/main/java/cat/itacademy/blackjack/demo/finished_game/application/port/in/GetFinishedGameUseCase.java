package cat.itacademy.blackjack.demo.finished_game.application.port.in;

import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.finished_games.dto.FinishedGamesResponseDto;

public interface GetFinishedGameUseCase {
    FinishedGamesResponseDto getGameById(String gameId);
}
