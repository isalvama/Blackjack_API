package cat.itacademy.blackjack.demo.game.domain.event;

import cat.itacademy.blackjack.demo.game.domain.model.Game;

import java.time.LocalDateTime;
import java.util.UUID;

public record GameFinishedEvent(
        UUID id,
        String playerName,
        Integer playerNumberOfCards,
        Integer dealerNumberOfCards,
        Integer totalCardsValuePlayer,
        Integer totalCardsValueDealer,
        String gameResult,
        Boolean finishedWithBlackjack,
        LocalDateTime createdAt
        ) {

    public static GameFinishedEvent from (Game game){
        return new GameFinishedEvent(
                game.getId().value(),
                game.getUserPlayer().getName().value(),
                game.getUserPlayer().getNumberOfCards(),
                game.getDealer().getNumberOfCards(),
                game.getUserPlayer().getHandValue(),
                game.getDealer().getHandValue(),
                game.getGameOutcome().result().name(),
                game.getGameOutcome().blackjack(),
                game.getCreatedAt()
                );
    }
}
