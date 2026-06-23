package cat.itacademy.blackjack.demo.finished_game.application.service;

import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetAllFinishedGamesSortedUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.repository.JpaFinishedGameRepository;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.FinishedGameResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllFinishedGamesSortedService implements GetAllFinishedGamesSortedUseCase {
    private final JpaFinishedGameRepository jpaFinishedGameRepository;

    @Override
    public List<FinishedGameResponseDto> getAllOrderedByFinishedAtDesc() {
        List<FinishedGame> finishedGames = jpaFinishedGameRepository.getAllOrderedByFinishedAtDesc();
        if (finishedGames.isEmpty()){
            return List.of();
        }
        return FinishedGameResponseDto.from(finishedGames);
    }

    @Override
    public List<FinishedGameResponseDto> getAllOrderedByScoreDesc() {
        return List.of();
    }
}
