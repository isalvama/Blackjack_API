package cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGameDto (
        @Schema(
                description = "Name of the player. Must not be empty and has a character limit.",
                example = "John Doe",
                minLength = 1,
                maxLength = 30,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        @Size(min = 1, max = 30)
        String name
){}
