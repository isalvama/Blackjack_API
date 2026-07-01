package cat.itacademy.blackjack.demo.finished_game.infrastructure.web.finished_games;

import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetFinishedGameUseCase;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetFinishedGamesSortedUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortCriteria;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortType;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.finished_games.dto.FinishedGamesResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Finished Games", description = "Endpoints for retrieving history and statistics of completed games")
public class FinishedGamesRestController {
    private final GetFinishedGamesSortedUseCase getFinishedGamesUseCase;
    private final GetFinishedGameUseCase getFinishedGameUseCase;

    @Operation(
            summary = "Get a finished game by ID",
            description = "Retrieves the full record of a specific completed game using its unique UUID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game found"),
            @ApiResponse(responseCode = "400", description = "Invalid input: the path variable is not a UUID", content = @Content),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FinishedGamesResponseDto> getGameById(
            @Parameter(description = "The UUID of the game", example = "3d558128-622e-47dd-91b9-54d2b93aed21")
            @PathVariable("id") @UUID String gameId) {
        return ResponseEntity.ok(getFinishedGameUseCase.getGameById(gameId));
    }

    @Operation(
            summary = "List and search finished games",
            description = "Returns a sorted list of completed games. You can filter by Player ID OR Player Name, but **not both simultaneously**."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of games retrieved successfully"),
            @ApiResponse(responseCode = "409", description = "Invalid search criteria (both ID and Name provided)")
    })
    @GetMapping
    public ResponseEntity<List<FinishedGamesResponseDto>> getGames(
            @Parameter(description = "Filter by unique Player ID. Cannot be used with playerName.", example = "1")
            @RequestParam(required = false) @Positive Long playerId,

            @Parameter(description = "Filter by Player Name. Cannot be used with playerId.", example = "John Doe")
            @RequestParam(required = false) @Size(min = 1, max = 30) String playerName,

            @Parameter(description = "Sort criteria for the results")
            @RequestParam(defaultValue = FinishedGameSortType.DEFAULT_SORT_VALUE) FinishedGameSortType sort
    ) {
        FinishedGameSortCriteria criteria = new FinishedGameSortCriteria(sort, Optional.ofNullable(playerId), Optional.ofNullable(playerName));
        return ResponseEntity.ok(getFinishedGamesUseCase.execute(criteria));
    }
}
