package cat.itacademy.blackjack.game_statistics.application.command;

import cat.itacademy.blackjack.game.infrastructure.event.GameFinishedEvent;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ProcessGameStatisticsCommand(
        UUID gameId,
        String userPlayerName,
        Integer numberOfRequestedCardsByUser,
        Integer numberOfRequestedCardsByDealer,
        Integer totalCardsValueUser,
        Integer totalCardsValueDealer,
        LocalDateTime createdAt,
        String gameResult,
        Boolean finishedWithBlackjack,
        LocalDateTime finishedAt
) {

    public static ProcessGameStatisticsCommand fromGameFinishedEvent (GameFinishedEvent event){
        return new ProcessGameStatisticsCommand(
                event.id(),
                event.userPlayerName(),
                event.userNumberOfCards(),
                event.dealerNumberOfCards(),
                event.totalCardsValueUser(),
                event.totalCardsValueDealer(),
                event.createdAt(),
                event.gameResult(),
                event.finishedWithBlackjack(),
                event.finishedAt()
        );
    }
}
