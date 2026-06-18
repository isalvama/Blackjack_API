package cat.itacademy.blackjack.demo.game.infrastructure.event;

import cat.itacademy.blackjack.demo.game.domain.model.Game;

import java.time.LocalDateTime;
import java.util.UUID;

public record GameFinishedEvent(
        UUID id,
        String userPlayerName,
        Integer userNumberOfCards,
        Integer dealerNumberOfCards,
        Integer totalCardsValueUser,
        Integer totalCardsValueDealer,
        LocalDateTime createdAt,
        String gameResult,
        Boolean finishedWithBlackjack,
        LocalDateTime finishedAt
) {

    public static GameFinishedEvent from (Game game){
        return new GameFinishedEvent(
                game.getId().value(),
                game.getUserPlayer().getName().value(),
                game.getUserPlayer().getNumberOfCards(),
                game.getDealer().getNumberOfCards(),
                game.getUserPlayer().getHandValue(),
                game.getDealer().getHandValue(),
                game.getCreatedAt(),
                game.getGameOutcome().result().name(),
                game.getGameOutcome().blackjack(),
                game.getGameOutcome().finishedAt()
        );
    }
}
