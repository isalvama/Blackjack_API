package cat.itacademy.blackjack.demo.finished_game.infrastructure.web.finished_games.dto;

import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Comprehensive record of a completed game")
public record FinishedGamesResponseDto(
        @Schema(description = "Internal database ID", example = "105")
        Long id,

        @Schema(description = "The UUID used during the active game phase", example = "3d558128-622e-47dd-91b9-54d2b93aed21")
        String gameId,

        @Schema(description = "Unique ID of the player", example = "1")
        Long playerId,

        @Schema(description = "Name of the player", example = "John Doe")
        String playerName,

        @Schema(description = "Number of cards the player drew", example = "3")
        Integer playerNumberOfCards,

        @Schema(description = "Number of cards the dealer drew", example = "2")
        Integer dealerNumberOfCards,

        @Schema(description = "Final value of player's hand", example = "20")
        Integer totalCardsValuePlayer,

        @Schema(description = "Final value of dealer's hand", example = "18")
        Integer totalCardsValueDealer,

        @Schema(description = "Final result of the match", allowableValues = {"PLAYER_WIN", "DEALER_WIN", "PUSH"}, example = "PLAYER_WIN")
        String gameResult,

        @Schema(description = "True if the game was a natural Blackjack", example = "false")
        Boolean finishedWithBlackjack,

        @Schema(description = "When the game session started")
        LocalDateTime createdAt,

        @Schema(description = "When the game was archived")
        LocalDateTime finishedAt,

        @Schema(description = "Calculated score for the player in this game", example = "100")
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



