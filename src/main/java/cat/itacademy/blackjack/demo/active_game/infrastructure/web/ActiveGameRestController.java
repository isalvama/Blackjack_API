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
    private final DeleteUseCase deleteUseCase;

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

    @DeleteMapping("{id}")
    public void deleteActiveGame(@PathVariable @UUID String id) {
        deleteUseCase.execute(id);
    }
}
