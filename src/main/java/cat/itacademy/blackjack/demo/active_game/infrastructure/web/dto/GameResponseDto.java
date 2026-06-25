package cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto;

import cat.itacademy.blackjack.demo.active_game.domain.model.Game;

import java.time.LocalDateTime;
import java.util.List;

public record GameResponseDto(
String id,
LocalDateTime createdAt,
LocalDateTime lastTimePlayedAt,
String username,
Integer playerTotalCardsValue,
List<CardDto> playerHand,
CardDto dealerFirstCard,
Integer dealerTotalCardsValue,
String gameState,
String gameResult,
Boolean finishedWithBlackjack,
List<CardDto> dealerFinalHand
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
                CardDto.from(game.getDealer().getHand().getCards().getFirst()),
                game.getDealer().getHandValue(),
                game.getGameState().name(),
                outcome != null ? game.getGameOutcome().result().name() : null,
                outcome != null ? game.getGameOutcome().blackjack() : null,
                outcome != null ? game.getDealer().getHand().getCards().stream().map(CardDto::from).toList() : null
        );
    }

    public static List<GameResponseDto> from (List<Game> games){
        if (games == null || games.isEmpty()){
            return List.of();
        }
        return games.stream().map(GameResponseDto::from).toList();
    }

    }
