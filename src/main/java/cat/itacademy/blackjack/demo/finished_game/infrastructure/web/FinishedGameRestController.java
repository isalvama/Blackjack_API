package cat.itacademy.blackjack.demo.finished_game.infrastructure.web;

import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetAllFinishedGamesSortedUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/api/finished-games")
@Validated
@RequiredArgsConstructor
public class FinishedGameRestController {
    private final GetAllFinishedGamesSortedUseCase getAllFinishedGamesUseCase;

    @GetMapping
    public ResponseEntity<List<FinishedGameResponseDto>> getAllFinishedGames() {
        List<FinishedGameResponseDto> gameResponses = getAllFinishedGamesUseCase.getAllOrderedByFinishedAtDesc();
        return ResponseEntity.ok(gameResponses);
    }
}
