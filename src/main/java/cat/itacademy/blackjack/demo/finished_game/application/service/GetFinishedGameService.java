package cat.itacademy.blackjack.demo.finished_game.application.service;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.finished_game.application.exception.FinishedGameNotFoundException;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetFinishedGameUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.repository.JpaFinishedGameRepository;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.FinishedGameResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetFinishedGameService implements GetFinishedGameUseCase {
    private final JpaFinishedGameRepository jpaFinishedGameRepository;

    @Override
    public FinishedGameResponseDto getGameById(String gameId) {
        Optional <FinishedGame> finishedGame = jpaFinishedGameRepository.findById(GameId.fromString(gameId));
        return finishedGame.map(FinishedGameResponseDto::from).orElseThrow(() -> new FinishedGameNotFoundException("No games found with id " + gameId)
        );
    }
}
