package cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto;

import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a playing card in the game")
public record CardDto(
        @Schema(
                description = "The suit of the card",
                allowableValues = {"CLUBS", "DIAMONDS", "HEARTS", "SPADES"},
                example = "SPADES"
        )
        String suit,
        @Schema(
                description = "The rank or number of the card",
                allowableValues = {"ACE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "JACK", "QUEEN", "KING"},
                example = "ACE"
        )
        String cardNumber
) {
    public static CardDto from (Card card){
        return new CardDto(
                card.suit().name(),
                card.cardNumber().name()
        );
    }
}
