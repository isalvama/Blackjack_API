package cat.itacademy.blackjack.demo.active_game.infrastructure.web;

import cat.itacademy.blackjack.demo.active_game.application.port.in.*;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.CreateGameDto;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.ActiveGameResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/active-games")
@Validated
@RequiredArgsConstructor
@Tag(name = "Active Games Management", description = "APIs for managing active games")
public class ActiveGameRestController {
    private final CreateGameUseCase createGameUseCase;
    private final GetActiveGameUseCase getActiveGameUseCase;
    private final GetAllActiveGamesUseCase getAllActiveGamesUseCase;
    private final HitUseCase hitUseCase;
    private final StandUseCase standUseCase;
    private final DeleteUseCase deleteUseCase;

    @Operation(summary = "Create and start a new game",
            description = "Creates a new Blackjack session using the provided player name. The initial deal consists of one card for the dealer and two cards for the player"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Game created successfully. Returns the information about the state of the game needed to continue playing (if it is still not over).",
                    content = @Content(schema = @Schema(implementation = ActiveGameResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (name is blank or too long) or business rule violation", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict: Game already exists in history. Occurs when archiving an immediate Blackjack result and a game with the same id has already exists in finished games history record", content = @Content)

    })
    @PostMapping
    public ResponseEntity<ActiveGameResponseDto> createNewGame(@Valid @RequestBody CreateGameDto request) {
        ActiveGameResponseDto gameResponse = createGameUseCase.execute(request.name());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(gameResponse.id())
                .toUri();

        return ResponseEntity.created(location).body(gameResponse);
    }

    @Operation(summary = "Get current game state", description = "Returns the id, status, hands and any other needed information to continue playing of a specific active game.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game found successfully. Returns the information about the state of a specific active game.",
                    content = @Content(schema = @Schema(implementation = ActiveGameResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input: the path variable is not a UUID", content = @Content),
            @ApiResponse(responseCode = "404", description = "Game not found", content = @Content),
    })
    @GetMapping("{id}")
    public ResponseEntity<ActiveGameResponseDto> getActiveGameState(
            @Parameter(description = "The unique UUID of the game") @PathVariable @UUID String id) {
        ActiveGameResponseDto gameResponse = getActiveGameUseCase.execute(id);
        return ResponseEntity.ok(gameResponse);
    }

    @Operation(summary = "List all active games", description = "Retrieves a list of all the games currently in progress with the id, status, hands and any other needed information to continue playing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the list of all the active games and the status information of each of them.",
                    content = @Content(schema = @Schema(implementation = ActiveGameResponseDto.class))),
    })
    @GetMapping
    public ResponseEntity<List<ActiveGameResponseDto>> getAllActiveGames() {
        List<ActiveGameResponseDto> gameResponses = getAllActiveGamesUseCase.execute();
        return ResponseEntity.ok(gameResponses);
    }

    @Operation(summary = "Hit (Draw a card)", description = "The player requests another card. If the total exceeds 21, the player busts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hit performed successfully. Returns the essential information about the state of the game after the hit",
                    content = @Content(schema = @Schema(implementation = ActiveGameResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input: the path variable is not a UUID", content = @Content),
            @ApiResponse(responseCode = "404", description = "Game not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict: Game already exists in history. Occurs when a game finishes after a hit and its ID is already present in the records.", content = @Content)
    })
    @PostMapping("{id}/hit")
    public ResponseEntity<ActiveGameResponseDto> hit(
            @Parameter(description = "The UUID of the game") @PathVariable @UUID String id) {
        ActiveGameResponseDto gameResponse = hitUseCase.execute(id);
        return ResponseEntity.ok(gameResponse);
    }

    @Operation(summary = "Stand (End turn)", description = "The player keeps their current hand. The dealer then plays their turn according to house rules.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stand performed successfully. Returns the game state details and the resolution causes",
                    content = @Content(schema = @Schema(implementation = ActiveGameResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input: the path variable is not a UUID", content = @Content),
            @ApiResponse(responseCode = "404", description = "Game not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict: Game already exists in history. Occurs when a game finishes after a hit and its ID is already present in the records.", content = @Content)
    })
    @PostMapping("{id}/stand")
    public ResponseEntity<ActiveGameResponseDto> stand(
            @Parameter(description = "The UUID of the game") @PathVariable @UUID String id) {
        ActiveGameResponseDto gameResponse = standUseCase.execute(id);
        return ResponseEntity.ok(gameResponse);
    }

    @Operation(summary = "Delete/Cancel a game", description = "Removes an active game from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Game deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input: the path variable is not a UUID", content = @Content),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActiveGame(
            @Parameter(description = "The UUID of the game to delete") @PathVariable @UUID String id) {
        deleteUseCase.execute(id);
    }
}
