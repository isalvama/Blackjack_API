package cat.itacademy.blackjack.demo.finished_game.application.service;

import cat.itacademy.blackjack.demo.finished_game.application.command.ProcessGameStatisticsCommand;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.ProcessFinishedGameUseCase;
import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.finished_game.application.port.out.FinishedGamePort;
import cat.itacademy.blackjack.demo.finished_game.application.port.out.PlayerProfilePort;
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

        PlayerProfile playerProfile = playerProfilePort.findByName(command.userPlayerName())
                .orElseGet(() -> {
                    Name playerName = Name.of(command.userPlayerName());
                    return PlayerProfile.create(playerName);
                });

        playerProfile.updateProfileWithNewGame(command.gameResult(), command.totalCardsValueUser().longValue(), command.finishedWithBlackjack());
        playerProfilePort.save(playerProfile);

        HandState userPlayerHandState = HandState.create(command.playerName(), command.playerName());
        HandState dealerPlayerHandState = HandState.create(command.numberOfRequestedCardsByDealer(), command.numberOfRequestedCardsByDealer());
        FinishedGame finishedGame = FinishedGame.create(
                GameId.fromUUID(command.gameId()),
                userPlayerHandState,
                dealerPlayerHandState,
                GameResult.fromString(command.gameResult()),
                command.finishedWithBlackjack(),
                command.createdAt());
        finishedGamePort.save(finishedGame);
    }
}
