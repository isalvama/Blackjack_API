package cat.itacademy.blackjack.demo.game_statistics.application.command;

import cat.itacademy.blackjack.demo.game.domain.event.GameFinishedEvent;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ProcessGameStatisticsCommand(
        UUID gameId,
        String userPlayerName,
        Integer playerName,
        Integer numberOfRequestedCardsByDealer,
        Integer totalCardsValueUser,
        Integer totalCardsValueDealer,
        LocalDateTime createdAt,
        String gameResult,
        Boolean finishedWithBlackjack
) {

    public static ProcessGameStatisticsCommand fromGameFinishedEvent (GameFinishedEvent event){
        return new ProcessGameStatisticsCommand(
                event.id(),
                event.playerName(),
                event.playerNumberOfCards(),
                event.dealerNumberOfCards(),
                event.totalCardsValuePlayer(),
                event.totalCardsValueDealer(),
                event.createdAt(),
                event.gameResult(),
                event.finishedWithBlackjack()
        );
    }
}
