package cat.itacademy.blackjack.demo.finished_game.application.mapper;

import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.finished_game.application.command.ProcessGameStatisticsCommand;
import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;
import cat.itacademy.blackjack.demo.finished_game.domain.value_object.HandState;
import org.springframework.stereotype.Component;

@Component
public class ProcessFinishedGameMapper {
    public FinishedGame toDomain(ProcessGameStatisticsCommand command){
        HandState userPlayerHandState = HandState.create(command.playerNumberOfCards(), command.totalCardsValuePlayer());
        HandState dealerPlayerHandState = HandState.create(command.dealerNumberOfCards(), command.totalCardsValueDealer());

        return FinishedGame.create(
                GameId.fromUUID(command.gameId()),
                userPlayerHandState,
                dealerPlayerHandState,
                GameResult.fromString(command.gameResult()),
                command.finishedWithBlackjack(),
                command.createdAt()
        );
    }
}
