package cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto;

import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;

public record CardDto(
        String suit,
        String cardNumber
) {
    public static CardDto from (Card card){
        return new CardDto(
                card.suit().name(),
                card.cardNumber().name()
        );
    }
}
