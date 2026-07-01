package cat.itacademy.blackjack.demo.finished_game.infrastructure.web.player_profiles;

import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetPlayerProfileUseCase;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetPlayerProfilesSortedUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.PlayerProfileSortType;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.player_profiles.dto.PlayerProfileResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/player-profiles")
@Validated
@RequiredArgsConstructor
@Tag(name = "Player Profiles", description = "Endpoints for player statistics, rankings, and profile lookups")
public class PlayerProfilesRestController {
    private final GetPlayerProfilesSortedUseCase getPlayerProfilesUseCase;
    private final GetPlayerProfileUseCase getPlayerProfileUseCase;

    @Operation(
            summary = "Get player profile by ID",
            description = "Retrieves the statistics and profile data for a specific player using their numeric database ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player found"),
            @ApiResponse(responseCode = "404", description = "Player not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlayerProfileResponseDto> getPlayerById(
            @Parameter(description = "Numeric ID of the player", example = "1")
            @PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(getPlayerProfileUseCase.getPlayerById(id));
    }

    @Operation(
            summary = "Get player profile by name",
            description = "Retrieves the statistics and profile data for a specific player using their name."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player found"),
            @ApiResponse(responseCode = "404", description = "Player not found with that name"),
            @ApiResponse(responseCode = "400", description = "Invalid name (blank or exceeds 30 characters)")
    })
    @GetMapping("/search")
    public ResponseEntity<PlayerProfileResponseDto> getPlayerByName(
            @Parameter(description = "Full name of the player", example = "John Doe")
            @RequestParam(required = true) @NotBlank @Size(min = 1, max = 30) String name) {
        return ResponseEntity.ok(getPlayerProfileUseCase.getPlayerByName(name));
    }

    @Operation(
            summary = "List all players (Rankings)",
            description = "Returns a list of all player profiles. Ideal for leaderboards and rankings."
    )
    @GetMapping
    public ResponseEntity<List<PlayerProfileResponseDto>> getPlayers(
            @Parameter(description = "Criteria to sort the players (e.g., by score or games played)")
            @RequestParam(defaultValue = PlayerProfileSortType.DEFAULT_SORT_VALUE) PlayerProfileSortType sort
    ) {
        return ResponseEntity.ok(getPlayerProfilesUseCase.execute(sort));
    }
}
