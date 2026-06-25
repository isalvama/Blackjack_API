package cat.itacademy.blackjack.demo.finished_game.application.service;

import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetFinishedGamesFilteredUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.repository.JpaFinishedGameRepository;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.FinishedGameResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class GetFinishedGamesFilteredService implements GetFinishedGamesFilteredUseCase {
    private final JpaFinishedGameRepository jpaFinishedGameRepository;

    @Override
    public List<FinishedGameResponseDto> getAllByPlayerId(Long playerId) {
        List<FinishedGame> finishedGames = jpaFinishedGameRepository.getByPlayerId(playerId);
        return FinishedGameResponseDto.from(finishedGames);
    }

    @Override
    public List<FinishedGameResponseDto> getAllByPlayerName(String name) {
        List<FinishedGame> finishedGames = jpaFinishedGameRepository.getByPlayerName(name);
        return FinishedGameResponseDto.from(finishedGames);
    }
}
