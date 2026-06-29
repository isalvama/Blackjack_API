package cat.itacademy.blackjack.demo.finished_game.infrastructure.web.finished_games;

import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetFinishedGameUseCase;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetFinishedGamesSortedUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortCriteria;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortType;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.finished_games.dto.FinishedGamesResponseDto;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/finished-games")
@Validated
@RequiredArgsConstructor
public class FinishedGamesRestController {
    private final GetFinishedGamesSortedUseCase getFinishedGamesUseCase;
    private final GetFinishedGameUseCase getFinishedGameUseCase;


    @GetMapping("/{id}")
    public ResponseEntity<FinishedGamesResponseDto> getGameById(@PathVariable("id") @UUID String gameId) {
        return ResponseEntity.ok(getFinishedGameUseCase.getGameById(gameId));
    }

    @GetMapping
    public ResponseEntity<List<FinishedGamesResponseDto>> getGames(
            @RequestParam(required = false) @Positive Long playerId,
            @RequestParam(required = false) @Size(min = 1, max = 30) String playerName,
            @RequestParam(defaultValue = FinishedGameSortType.DEFAULT_SORT_VALUE) FinishedGameSortType sort
    ) {
        FinishedGameSortCriteria criteria = new FinishedGameSortCriteria(sort, Optional.ofNullable(playerId), Optional.ofNullable(playerName));
        return ResponseEntity.ok(getFinishedGamesUseCase.execute(criteria));
    }
}
