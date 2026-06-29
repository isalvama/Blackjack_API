package cat.itacademy.blackjack.demo.active_game.infrastructure.web;

import cat.itacademy.blackjack.demo.active_game.application.port.in.*;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.CreateGameDto;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.GameResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * GameRestController exposes game management operations as REST endpoints.
 *
 * <p>In hexagonal architecture terms, this is a <strong>primary adapter</strong> (driving adapter)
 * that adapts HTTP requests to calls on the application's primary ports. It provides a REST API
 * for interacting with the core application functionality.</p>
 *
 * <p>This controller handles the following operations:</p>
 * <ul>
 *   <li>Game creation and intial state information</li>
 *   <li>Active Game State retrieval</li> TODO
 *   <li>Stock purchases and sales</li> TODO
 *   <li>Transaction history retrieval</li> TODO
 * </ul>
 */
@RestController
@RequestMapping("/api/active-games")
@Validated
@RequiredArgsConstructor
public class ActiveGameRestController {
    private final CreateGameUseCase createGameUseCase;
    private final GetActiveGameUseCase getActiveGameUseCase;
    private final GetAllActiveGamesUseCase getAllActiveGamesUseCase;
    private final HitUseCase hitUseCase;
    private final StandUseCase standUseCase;


    /**
     * Creates a new game.
     *
     * <p>POST /api/active-games</p>
     *
     * <p>Returns HTTP 201 Created with a Location header pointing to the newly created game and the information of the game state
     * created resource at {@code /api/active-games/{id}}.</p>
     *
     * @param request DTO containing the player name
     * @return The newly created game state information with HTTP 201 Created status
     *         and Location header
     */
    @PostMapping
    public ResponseEntity<GameResponseDto> createNewGame(@Valid @RequestBody CreateGameDto request) {
        GameResponseDto gameResponse = createGameUseCase.execute(request.name());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(gameResponse.id())
                .toUri();

        return ResponseEntity.created(location).body(gameResponse);
    }

    @GetMapping("{id}")
    public ResponseEntity<GameResponseDto> getActiveGameState(@PathVariable @UUID String id) {
        GameResponseDto gameResponse = getActiveGameUseCase.execute(id);
        return ResponseEntity.ok(gameResponse);
    }

    @GetMapping
    public ResponseEntity<List<GameResponseDto>> getAllActiveGames() {
        List<GameResponseDto> gameResponses = getAllActiveGamesUseCase.execute();
        return ResponseEntity.ok(gameResponses);
    }

    @PostMapping("{id}/hit")
    public ResponseEntity<GameResponseDto> hit(@PathVariable @UUID String id) {
        GameResponseDto gameResponse = hitUseCase.execute(id);
        return ResponseEntity.ok(gameResponse);
    }

    @PostMapping("{id}/stand")
    public ResponseEntity<GameResponseDto> stand(@PathVariable @UUID String id) {
        GameResponseDto gameResponse = standUseCase.execute(id);
        return ResponseEntity.ok(gameResponse);
    }
}
