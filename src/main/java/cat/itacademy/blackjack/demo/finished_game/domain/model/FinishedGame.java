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
        this.gameId = validateNotNull(gameId, "gameId");
        this.playerHandState = validateNotNull(playerHandState, "playerHandState");
        this.dealerHandState = validateNotNull(dealerHandState, "dealerHandState");
        this.gameResult = validateNotNull(gameResult, "gameResult");
        this.finishedWithBlackjack = validateNotNull(finishedWithBlackjack, "finishedWithBlackjack");
        this.createdAt = validateNotNull(createdAt, "createdAt");
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
        FinishedGame finishedGame = new FinishedGame(
                gameId,
                playerHandState,
                dealerHandState,
                gameResult,
                finishedWithBlackjack,
                createdAt
       );
       finishedGame.id = validateId(id, "id");
       finishedGame.playerId = validateId(playerId, "playerId");
       finishedGame.playerName = validateNotNull(playerName, "playerName");
       finishedGame.finishedAt = validateNotNull(finishedAt, "finishedAt");
        if (score == null) throw new InvalidFinishedGameException("score cannot be null");
        if (score < 0) throw new InvalidFinishedGameException("score cannot be negative");
        finishedGame.score = score;
       return finishedGame;
    }

    public void addPlayerProfileInfo(Long playerId, Name playerName) {
        this.playerId = validateNotNull(playerId, "playerId");
        this.playerName = validateNotNull(playerName, "playerName");
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

    private static <T> T validateNotNull(T obj, String paramName) {
        if (obj == null)
            throw new InvalidFinishedGameException(paramName + " cannot be null");
        return obj;
    }

    private static Long validateId(Long param, String paramName) {
        if (param == null)
            throw new InvalidFinishedGameException(paramName + " cannot be null");
        if (param < 0)
            throw new InvalidFinishedGameException(paramName + " cannot be negative");
        return param;
    }
}
