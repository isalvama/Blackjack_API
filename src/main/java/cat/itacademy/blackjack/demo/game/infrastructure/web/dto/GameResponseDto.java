package cat.itacademy.blackjack.demo.game.infrastructure.web.dto;

import cat.itacademy.blackjack.demo.game.domain.model.Game;

import java.time.LocalDateTime;
import java.util.List;

public record GameResponseDto(
String id,
LocalDateTime createdAt,
LocalDateTime lastTimePlayedAt,
String username,
Integer totalCardsValue,
List<CardDto> hand,
String gameState,
String gameResult,
Boolean finishedWithBlackjack,
LocalDateTime finishedAt

) {
    public static GameResponseDto from (Game game){
        var outcome = game.getGameOutcome();

        return new GameResponseDto(
                game.getId().value().toString(),
                game.getCreatedAt(),
                game.getLastTimePlayedAt(),
                game.getUserPlayer().getName().value(),
                game.getUserPlayer().getHandValue(),
                game.getUserPlayer().getHand().getCards().stream().map(CardDto::from).toList(),
                game.getGameState().name(),
                outcome != null ? game.getGameOutcome().result().name() : null,
                outcome != null ? game.getGameOutcome().blackjack() : null,
                outcome != null ? game.getGameOutcome().finishedAt() : null
        );
    }
}
