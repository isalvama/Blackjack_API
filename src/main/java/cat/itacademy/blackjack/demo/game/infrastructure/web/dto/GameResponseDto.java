package cat.itacademy.blackjack.demo.game.infrastructure.web.dto;

import cat.itacademy.blackjack.demo.game.domain.model.Game;
import cat.itacademy.blackjack.demo.game.domain.value_object.Card;

import java.time.LocalDateTime;
import java.util.List;

public record GameResponseDto(
String id,
LocalDateTime createdAt,
LocalDateTime lastTimePlayedAt,
String username,
Integer playerTotalCardsValue,
List<CardDto> playerHand,
Card dealerFirstCard,
String gameState,
String gameResult,
Boolean finishedWithBlackjack
) {
    public static GameResponseDto from (Game game){
        var outcome = game.getGameOutcome();

        return new GameResponseDto(
                game.getId().value().toString(),
                game.getCreatedAt(),
                game.getLastTimePlayedAt(),
                game.getUserPlayer().getName().name(),
                game.getUserPlayer().getHandValue(),
                game.getUserPlayer().getHand().getCards().stream().map(CardDto::from).toList(),
                game.getDealer().getHand().getCards().getFirst(),
                game.getGameState().name(),
                outcome != null ? game.getGameOutcome().result().name() : null,
                outcome != null ? game.getGameOutcome().blackjack() : null
        );
    }
}
