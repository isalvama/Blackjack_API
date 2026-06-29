package cat.itacademy.blackjack.demo.finished_game.infrastructure.web.finished_games.dto;

import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;

import java.time.LocalDateTime;
import java.util.List;

public record FinishedGamesResponseDto(
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
       LocalDateTime finishedAt,
       Integer score
) {

    public static FinishedGamesResponseDto from(FinishedGame finishedGame) {
        return new FinishedGamesResponseDto(
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
                finishedGame.getFinishedAt(),
                finishedGame.getScore()
        );
    }

    public static List<FinishedGamesResponseDto> from(List<FinishedGame> finishedGames) {
        return finishedGames.stream().map(FinishedGamesResponseDto::from).toList();
    }
}



