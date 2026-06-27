package cat.itacademy.blackjack.demo.finished_game.infrastructure.web;

import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetFinishedGameUseCase;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetFinishedGamesSortedUseCase;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetPlayerProfileUseCase;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetPlayerProfilesSortedUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortCriteria;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortType;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.PlayerProfileSortType;
import jakarta.validation.constraints.NotBlank;
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
public class FinishedGameRestController {
    private final GetFinishedGamesSortedUseCase getFinishedGamesUseCase;
    private final GetFinishedGameUseCase getFinishedGameUseCase;
    private final GetPlayerProfilesSortedUseCase getPlayerProfilesUseCase;
    private final GetPlayerProfileUseCase getPlayerProfileUseCase;


    @GetMapping("{id}")
    public ResponseEntity<FinishedGameResponseDto> getGameById(@PathVariable @UUID String gameId) {
        return ResponseEntity.ok(getFinishedGameUseCase.getGameById(gameId));
    }

    @GetMapping
    public ResponseEntity<List<FinishedGameResponseDto>> getGames(
            @RequestParam(required = false) Long playerId,
            @RequestParam(required = false) String playerName,
            @RequestParam(defaultValue = FinishedGameSortType.DEFAULT_SORT_VALUE) FinishedGameSortType sort
    ) {
        FinishedGameSortCriteria criteria = new FinishedGameSortCriteria(sort, Optional.ofNullable(playerId), Optional.ofNullable(playerName));
        return ResponseEntity.ok(getFinishedGamesUseCase.execute(criteria));
    }

    @GetMapping("{id}")
    public ResponseEntity<PlayerProfileResponseDto> getPlayerById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(getPlayerProfileUseCase.getPlayerById(id));
    }

    @GetMapping("{name}")
    public ResponseEntity<PlayerProfileResponseDto> getPlayerByName(@NotBlank @Size(min = 1, max = 30) String name) {
        return ResponseEntity.ok(getPlayerProfileUseCase.getPlayerByName(name));
    }

    @GetMapping
    public ResponseEntity<List<PlayerProfileResponseDto>> getPlayers(
            @RequestParam(defaultValue = FinishedGameSortType.DEFAULT_SORT_VALUE) PlayerProfileSortType sort
    ) {
        return ResponseEntity.ok(getPlayerProfilesUseCase.execute(sort));
    }
}
