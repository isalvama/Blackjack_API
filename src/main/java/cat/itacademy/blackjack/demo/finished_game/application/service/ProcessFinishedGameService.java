package cat.itacademy.blackjack.demo.finished_game.application.service;

import cat.itacademy.blackjack.demo.finished_game.application.command.ProcessGameStatisticsCommand;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.ProcessFinishedGameUseCase;
import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.finished_game.application.port.out.FinishedGamePort;
import cat.itacademy.blackjack.demo.finished_game.application.port.out.PlayerProfilePort;
import cat.itacademy.blackjack.demo.finished_game.domain.exception.InvalidFinishedGameException;
import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;
import cat.itacademy.blackjack.demo.finished_game.domain.model.PlayerProfile;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.finished_game.domain.value_object.HandState;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessFinishedGameService implements ProcessFinishedGameUseCase {
    private final PlayerProfilePort playerProfilePort;
    private final FinishedGamePort finishedGamePort;

    @Transactional
    @Override
    public void execute(ProcessGameStatisticsCommand command) {

         if (finishedGamePort.findById(GameId.fromUUID(command.gameId())).isPresent()){
            throw new InvalidFinishedGameException("the new finished game cannot be saved because a game with id " + command.gameId().toString() + " already exists.");
        }

        PlayerProfile playerProfile = playerProfilePort.findByName(command.playerName())
                .orElseGet(() -> {
                    Name playerName = Name.of(command.playerName());
                    return PlayerProfile.create(playerName);
                });

        HandState userPlayerHandState = HandState.create(command.playerNumberOfCards(), command.totalCardsValuePlayer());
        HandState dealerPlayerHandState = HandState.create(command.dealerNumberOfCards(), command.totalCardsValueDealer());
        FinishedGame finishedGame = FinishedGame.create(
                GameId.fromUUID(command.gameId()),
                userPlayerHandState,
                dealerPlayerHandState,
                GameResult.fromString(command.gameResult()),
                command.finishedWithBlackjack(),
                command.createdAt()
        );

        playerProfile.updateProfileWithNewGame(finishedGame.getScore());

        PlayerProfile playerProfileUpdated = playerProfilePort.save(playerProfile);

        finishedGame.addPlayerProfileInfo(playerProfileUpdated.getId(), playerProfileUpdated.getName());

        finishedGamePort.save(finishedGame);
    }
}
