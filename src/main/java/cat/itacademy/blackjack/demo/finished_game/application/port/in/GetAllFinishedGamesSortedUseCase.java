package cat.itacademy.blackjack.demo.finished_game.application.port.in;

import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.FinishedGameResponseDto;

import java.util.List;

public interface GetAllFinishedGamesSortedUseCase {
    List<FinishedGameResponseDto> getAllOrderedByFinishedAtDesc();
    List<FinishedGameResponseDto> getAllOrderedByScoreDesc();

}
