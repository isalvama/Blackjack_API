package cat.itacademy.blackjack.demo.game_statistics.domain.model;

import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.game_statistics.domain.exception.InvalidFinishedGameException;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.game_statistics.domain.value_object.HandState;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FinishedGame{
    private Long id;
    private GameId gameId;
    private Long playerId;
    private HandState playerHandState;
    private HandState dealerHandState;
    private GameResult gameResult;
    private Boolean finishedWithBlackjack;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;

    private FinishedGame(GameId gameId, HandState playerHandState, HandState dealerHandState, GameResult gameResult, Boolean finishedWithBlackjack, LocalDateTime createdAt) {
        this.gameId = validateNotNull(gameId, "gameId cannot be null");
        this.playerHandState = validateNotNull(playerHandState, "playerHandState cannot be null");
        this.dealerHandState = validateNotNull(dealerHandState, "dealerHandState cannot be null");
        this.gameResult = validateNotNull(gameResult, "gameResult cannot be null");
        this.finishedWithBlackjack = validateNotNull(finishedWithBlackjack, "finishedWithBlackjack cannot be null");
        this.createdAt = validateNotNull(createdAt, "createdAt cannot be null");
    }

    public static FinishedGame create (GameId gameId, HandState playerHandState, HandState dealerHandState, GameResult gameResult, Boolean finishedWithBlackjack, LocalDateTime createdAt) {
        return new FinishedGame(
                gameId,
                playerHandState,
                dealerHandState,
                gameResult,
                finishedWithBlackjack,
                createdAt
        );
    }

    public static FinishedGame reconstitute(Long id, GameId gameId, Long playerId, HandState playerHandState, HandState dealerHandState, GameResult gameResult, Boolean finishedWithBlackjack, LocalDateTime createdAt, LocalDateTime finishedAt) {
       FinishedGame finishedGame = new FinishedGame(
                gameId,
                playerHandState,
                dealerHandState,
                gameResult,
                finishedWithBlackjack,
                createdAt
        );
       finishedGame.id = validateNotNull(id, "id cannot be null");
       finishedGame.playerId = validateNotNull(playerId, "playerId cannot be null");
       finishedGame.finishedAt = validateNotNull(finishedAt, "finishedAt cannot be null");
       return finishedGame;
    }

    private static <T> T validateNotNull(T obj, String message) {
        if (obj == null)
            throw new InvalidFinishedGameException(message);
        return obj;
    }
}
