package cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGameDto (
        @NotBlank
        @Size(min = 1, max = 30)
        String name
){

}
