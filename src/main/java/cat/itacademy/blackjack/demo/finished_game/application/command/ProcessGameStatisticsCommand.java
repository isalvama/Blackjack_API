package cat.itacademy.blackjack.demo.finished_game.application.command;

import cat.itacademy.blackjack.demo.active_game.domain.event.GameFinishedEvent;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ProcessGameStatisticsCommand(
        UUID gameId,
        String playerName,
        Integer playerNumberOfCards,
        Integer dealerNumberOfCards,
        Integer totalCardsValuePlayer,
        Integer totalCardsValueDealer,
        String gameResult,
        Boolean finishedWithBlackjack,
        LocalDateTime createdAt
) {

    public static ProcessGameStatisticsCommand fromGameFinishedEvent (GameFinishedEvent event){
        return new ProcessGameStatisticsCommand(
                event.id(),
                event.playerName(),
                event.playerNumberOfCards(),
                event.dealerNumberOfCards(),
                event.totalCardsValuePlayer(),
                event.totalCardsValueDealer(),
                event.gameResult(),
                event.finishedWithBlackjack(),
                event.createdAt()
                );
    }
}
