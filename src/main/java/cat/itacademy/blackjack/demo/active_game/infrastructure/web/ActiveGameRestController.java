package cat.itacademy.blackjack.demo.active_game.infrastructure.web;

import cat.itacademy.blackjack.demo.active_game.application.port.in.*;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.CreateGameDto;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.ActiveGameResponseDto;
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
    public ResponseEntity<ActiveGameResponseDto> createNewGame(@Valid @RequestBody CreateGameDto request) {
        ActiveGameResponseDto gameResponse = createGameUseCase.execute(request.name());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(gameResponse.id())
                .toUri();

        return ResponseEntity.created(location).body(gameResponse);
    }

    @GetMapping("{id}")
    public ResponseEntity<ActiveGameResponseDto> getActiveGameState(@PathVariable @UUID String id) {
        ActiveGameResponseDto gameResponse = getActiveGameUseCase.execute(id);
        return ResponseEntity.ok(gameResponse);
    }

    @GetMapping
    public ResponseEntity<List<ActiveGameResponseDto>> getAllActiveGames() {
        List<ActiveGameResponseDto> gameResponses = getAllActiveGamesUseCase.execute();
        return ResponseEntity.ok(gameResponses);
    }

    @PostMapping("{id}/hit")
    public ResponseEntity<ActiveGameResponseDto> hit(@PathVariable @UUID String id) {
        ActiveGameResponseDto gameResponse = hitUseCase.execute(id);
        return ResponseEntity.ok(gameResponse);
    }

    @PostMapping("{id}/stand")
    public ResponseEntity<ActiveGameResponseDto> stand(@PathVariable @UUID String id) {
        ActiveGameResponseDto gameResponse = standUseCase.execute(id);
        return ResponseEntity.ok(gameResponse);
    }
}
