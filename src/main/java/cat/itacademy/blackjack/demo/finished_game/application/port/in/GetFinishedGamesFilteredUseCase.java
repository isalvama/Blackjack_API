package cat.itacademy.blackjack.demo.finished_game.application.port.in;

import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.FinishedGameResponseDto;

import java.util.List;

public interface GetFinishedGamesFilteredUseCase {

    List<FinishedGameResponseDto> getAllByPlayerId(Long playerId);
    List<FinishedGameResponseDto> getAllByPlayerName(String name);
}
