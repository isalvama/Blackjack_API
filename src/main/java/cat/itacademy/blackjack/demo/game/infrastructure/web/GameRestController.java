package cat.itacademy.blackjack.demo.game.infrastructure.web;

import cat.itacademy.blackjack.demo.game.application.port.in.CreateGameUseCase;
import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.CreateGameDto;
import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.GameResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
 *   <li>Cash deposits and withdrawals</li> TODO
 *   <li>Stock purchases and sales</li> TODO
 *   <li>Transaction history retrieval</li> TODO
 * </ul>
 */
@RestController
@RequestMapping("/api/blackjack")
@RequiredArgsConstructor
public class GameRestController {
    private final CreateGameUseCase createGameUseCase;

    /**
     * Creates a new game.
     *
     * <p>POST /api/blackjack</p>
     *
     * <p>Returns HTTP 201 Created with a Location header pointing to the newly created game and the information of the game state
     * created resource at {@code /api/blackjack/{id}}.</p>
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
    public ResponseEntity<GameResponseDto> getActiveGameState(@PathVariable String id) {
        GameResponseDto gameResponse = createGameUseCase.execute(id);
        return ResponseEntity.ok(gameResponse);
    }

}
