package cat.itacademy.blackjack.demo.finished_game.infrastructure.web.player_profiles;

import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetPlayerProfileUseCase;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.GetPlayerProfilesSortedUseCase;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.PlayerProfileSortType;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.player_profiles.dto.PlayerProfileResponseDto;
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
public class PlayerProfilesRestController {
    private final GetPlayerProfilesSortedUseCase getPlayerProfilesUseCase;
    private final GetPlayerProfileUseCase getPlayerProfileUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<PlayerProfileResponseDto> getPlayerById(@PathVariable("id") @Positive Long id) {
        return ResponseEntity.ok(getPlayerProfileUseCase.getPlayerById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<PlayerProfileResponseDto> getPlayerByName(@RequestParam(required = true) @NotBlank @Size(min = 1, max = 30) String name) {
        return ResponseEntity.ok(getPlayerProfileUseCase.getPlayerByName(name));
    }

    @GetMapping
    public ResponseEntity<List<PlayerProfileResponseDto>> getPlayers(
            @RequestParam(defaultValue = PlayerProfileSortType.DEFAULT_SORT_VALUE) PlayerProfileSortType sort
    ) {
        return ResponseEntity.ok(getPlayerProfilesUseCase.execute(sort));
    }
}
