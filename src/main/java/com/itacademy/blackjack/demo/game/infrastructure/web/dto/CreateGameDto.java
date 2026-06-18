package cat.itacademy.blackjack.game.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGameDto (
        @NotBlank
        @Size(min = 1, max = 50)
        String name
){

}
