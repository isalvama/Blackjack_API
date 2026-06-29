package cat.itacademy.blackjack.demo.finished_game.application.port.in;

import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortCriteria;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.finished_games.dto.FinishedGamesResponseDto;

import java.util.List;

public interface GetFinishedGamesSortedUseCase {
    List<FinishedGamesResponseDto> execute(FinishedGameSortCriteria criteria);
}
