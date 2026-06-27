package cat.itacademy.blackjack.demo.finished_game.infrastructure.web;

import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;

import java.time.LocalDateTime;
import java.util.List;

public record FinishedGameResponseDto (
       Long id,
       String gameId,
       Long playerId,
       String playerName,
       Integer playerNumberOfCards,
       Integer dealerNumberOfCards,
       Integer totalCardsValuePlayer,
       Integer totalCardsValueDealer,
       String gameResult,
       Boolean finishedWithBlackjack,
       LocalDateTime createdAt,
       LocalDateTime finishedAt) {

    public static FinishedGameResponseDto from(FinishedGame finishedGame) {
        return new FinishedGameResponseDto(
                finishedGame.getId(),
                finishedGame.getGameId().toString(),
                finishedGame.getPlayerId(),
                finishedGame.getPlayerName().name(),
                finishedGame.getPlayerHandState().numberOfRequestedCards(),
                finishedGame.getDealerHandState().numberOfRequestedCards(),
                finishedGame.getPlayerHandState().totalCardsValue(),
                finishedGame.getDealerHandState().totalCardsValue(),
                finishedGame.getGameResult().name(),
                finishedGame.getFinishedWithBlackjack(),
                finishedGame.getCreatedAt(),
                finishedGame.getFinishedAt()
        );
    }

    public static List<FinishedGameResponseDto> from(List<FinishedGame> finishedGames) {
        return finishedGames.stream().map(FinishedGameResponseDto::from).toList();
    }
}



