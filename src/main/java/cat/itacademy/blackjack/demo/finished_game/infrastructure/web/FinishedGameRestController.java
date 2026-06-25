package cat.itacademy.blackjack.demo.finished_game.infrastructure.web;

import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetFinishedGamesSortedUseCase;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetFinishedGamesFilteredUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortCriteria;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.SortType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/finished-games")
@Validated
@RequiredArgsConstructor
public class FinishedGameRestController {
    private final GetFinishedGamesSortedUseCase getFinishedGamesUseCase;

    @GetMapping
    public ResponseEntity<List<FinishedGameResponseDto>> getGames(
            @RequestParam(required = false) Long playerId,
            @RequestParam(required = false) String playerName,
            @RequestParam(defaultValue = SortType.DEFAULT_SORT_VALUE) SortType sort
    ) {
        FinishedGameSortCriteria criteria = new FinishedGameSortCriteria(sort, Optional.ofNullable(playerId), Optional.ofNullable(playerName));
        List<FinishedGameResponseDto> gameResponses = getFinishedGamesUseCase.execute(criteria);
        return ResponseEntity.ok(gameResponses);
    }

}
