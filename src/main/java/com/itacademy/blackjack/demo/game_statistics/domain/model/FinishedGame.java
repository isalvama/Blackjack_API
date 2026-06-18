package cat.itacademy.blackjack.game_statistics.domain.model;

import cat.itacademy.blackjack.common.domain.GameResult;
import cat.itacademy.blackjack.game_statistics.domain.exception.InvalidFinishedGameException;
import cat.itacademy.blackjack.common.domain.value_object.GameId;
import cat.itacademy.blackjack.game_statistics.domain.value_object.HandState;

import java.time.LocalDateTime;

public class FinishedGame{
    private GameId id;
    private Long userPlayerId;
    private Integer gameNumber;
    private HandState userPlayerHandState;
    private HandState dealerHandState;
    private GameResult gameResult;
    private Boolean finishedWithBlackjack;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;

    private FinishedGame(GameId id, Long playerId, Integer gameNumber, HandState userPlayerHandState, HandState dealerHandState, GameResult gameResult, Boolean finishedWithBlackjack, LocalDateTime createdAt, LocalDateTime finishedAt) {
        if (id == null){
            throw new InvalidFinishedGameException("id cannot be null");
        }
        if (gameNumber == null){
            throw new InvalidFinishedGameException("gameNumber cannot be null");
        }
        if (gameNumber < 1){
            throw new InvalidFinishedGameException("gameNumber cannot be smaller than 1");
        }
        if (userPlayerHandState == null){
            throw new InvalidFinishedGameException("userPlayerHandState cannot be null");
        }
        if (dealerHandState == null){
            throw new InvalidFinishedGameException("dealerHandState cannot be null");
        }
        if (gameResult == null){
            throw new InvalidFinishedGameException("gameResult cannot be null");
        }
        if (finishedWithBlackjack == null){
            throw new InvalidFinishedGameException("finishedWithBlackjack cannot be null");
        }
        if (createdAt == null){
            throw new InvalidFinishedGameException("createdAt cannot be null");
        }
        if (finishedAt == null){
            throw new InvalidFinishedGameException("finishedAt cannot be null");
        }
        this.id = id;
        this.userPlayerId = playerId;
        this.gameNumber = gameNumber;
        this.gameResult = gameResult;
        this.finishedWithBlackjack = finishedWithBlackjack;
        this.createdAt = createdAt;
        this.finishedAt = finishedAt;
    }

    public static FinishedGame create (GameId id, Integer gameNumber, HandState userPlayerHandState, HandState dealerHandState, GameResult gameResult, Boolean finishedWithBlackjack, LocalDateTime createdAt, LocalDateTime finishedAt) {
        return new FinishedGame(
                id,
                null,
                gameNumber,
                userPlayerHandState,
                dealerHandState,
                gameResult,
                finishedWithBlackjack,
                createdAt,
                finishedAt
        );
    }

    public static FinishedGame reconstitute(GameId id, Long playerId, Integer gameNumber, HandState userPlayerHandState, HandState dealerHandState, GameResult gameResult, Boolean finishedWithBlackjack, LocalDateTime createdAt, LocalDateTime finishedAt) {
       return new FinishedGame(
               // passar param com primitius
                id,
                playerId,
                gameNumber,
                userPlayerHandState,
                dealerHandState,
                gameResult,
                finishedWithBlackjack,
                createdAt,
                finishedAt
        );
    }

    public GameId getId() {
        return id;
    }

    public Long getUserPlayerId() {
        return userPlayerId;
    }

    public Integer getGameNumber() {
        return gameNumber;
    }

    public HandState getUserPlayerHandState() {
        return userPlayerHandState;
    }

    public HandState getDealerHandState() {
        return dealerHandState;
    }

    public GameResult getGameResult() {
        return gameResult;
    }

    public Boolean getFinishedWithBlackjack() {
        return finishedWithBlackjack;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }
}
