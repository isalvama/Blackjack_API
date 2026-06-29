package cat.itacademy.blackjack.demo.finished_game.application.service;

import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetFinishedGamesSortedUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortCriteria;
import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.repository.JpaFinishedGameRepository;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.finished_games.dto.FinishedGamesResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetFinishedGamesSortedService implements GetFinishedGamesSortedUseCase {
    private final JpaFinishedGameRepository jpaFinishedGameRepository;


    @Override
    public List<FinishedGamesResponseDto> execute(FinishedGameSortCriteria criteria) {
        List<FinishedGame> finishedGames = jpaFinishedGameRepository.getByCriteria(criteria);
        return FinishedGamesResponseDto.from(finishedGames);
    }
}
