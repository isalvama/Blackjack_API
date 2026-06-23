package cat.itacademy.blackjack.demo.finished_game.domain.model;

import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.finished_game.domain.exception.InvalidFinishedGameException;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.finished_game.domain.value_object.HandState;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FinishedGame{
    private Long id;
    private GameId gameId;
    private Long playerId;
    private Name playerName;
    private HandState playerHandState;
    private HandState dealerHandState;
    private GameResult gameResult;
    private Boolean finishedWithBlackjack;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
    private Integer score;

    private FinishedGame(GameId gameId, HandState playerHandState, HandState dealerHandState, GameResult gameResult, Boolean finishedWithBlackjack, LocalDateTime createdAt) {
        this.gameId = validateNotNull(gameId, "gameId cannot be null");
        this.playerHandState = validateNotNull(playerHandState, "playerHandState cannot be null");
        this.dealerHandState = validateNotNull(dealerHandState, "dealerHandState cannot be null");
        this.gameResult = validateNotNull(gameResult, "gameResult cannot be null");
        this.finishedWithBlackjack = validateNotNull(finishedWithBlackjack, "finishedWithBlackjack cannot be null");
        this.createdAt = validateNotNull(createdAt, "createdAt cannot be null");
        this.finishedAt = validateNotNull(finishedAt, "finishedAt cannot be null");
    }

    public static FinishedGame create (GameId gameId, HandState playerHandState, HandState dealerHandState, GameResult gameResult, Boolean finishedWithBlackjack, LocalDateTime createdAt) {
        FinishedGame finishedGame = new FinishedGame(
                gameId,
                playerHandState,
                dealerHandState,
                gameResult,
                finishedWithBlackjack,
                createdAt
        );
        finishedGame.setScore();
        return finishedGame;
    }

    public static FinishedGame reconstitute(Long id, GameId gameId, Long playerId, Name playerName, HandState playerHandState, HandState dealerHandState, GameResult gameResult, Boolean finishedWithBlackjack, LocalDateTime createdAt, LocalDateTime finishedAt, Integer score) {
        if (score < 0){
            throw new InvalidFinishedGameException("score cannot be negative");
        }
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
       finishedGame.playerName = validateNotNull(playerName, "playerName cannot be null");
       finishedGame.finishedAt = validateNotNull(finishedAt, "finishedAt cannot be null");
       finishedGame.score = validateNotNull(score, "score cannot be null");
       return finishedGame;
    }

    public void addPlayerProfileInfo(Long playerId, Name playerName) {
        this.playerId = validateNotNull(playerId, "playerId cannot be null");
        this.playerName = validateNotNull(playerName, "playerName cannot be null");
    }

    private void setScore() {
        if (gameResult == GameResult.USER_WIN) {
            this.score = finishedWithBlackjack ? 21 : playerHandState.totalCardsValue();
        } else if (gameResult == GameResult.TIE) {
            this.score = finishedWithBlackjack ? 11 : playerHandState.totalCardsValue() / 2;
        } else {
            this.score = 0;
        }
    }

    private static <T> T validateNotNull(T obj, String message) {
        if (obj == null)
            throw new InvalidFinishedGameException(message);
        return obj;
    }
}
