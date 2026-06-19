package cat.itacademy.blackjack.demo.game_statistics.application.service;

import cat.itacademy.blackjack.demo.game_statistics.application.command.ProcessGameStatisticsCommand;
import cat.itacademy.blackjack.demo.game_statistics.application.port.in.ProcessGameStatisticsUseCase;
import cat.itacademy.blackjack.demo.common.domain.GameResult;
import cat.itacademy.blackjack.demo.game_statistics.application.port.out.FinishedGamePort;
import cat.itacademy.blackjack.demo.game_statistics.application.port.out.PlayerProfilePort;
import cat.itacademy.blackjack.demo.game_statistics.domain.model.FinishedGame;
import cat.itacademy.blackjack.demo.game_statistics.domain.model.PlayerProfile;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.game_statistics.domain.value_object.HandState;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessGameStatisticsService implements ProcessGameStatisticsUseCase {
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

        playerProfile.updateProfileWithNewGame(GameResult.USER_WIN, command.totalCardsValueUser().longValue(), command.finishedWithBlackjack());
        playerProfilePort.save(playerProfile);

        HandState userPlayerHandState = HandState.create(command.playerName(), command.playerName());
        HandState dealerPlayerHandState = HandState.create(command.numberOfRequestedCardsByDealer(), command.numberOfRequestedCardsByDealer());
        FinishedGame finishedGame = FinishedGame.create(
                GameId.fromUUID(command.gameId()),
                userPlayerHandState,
                dealerPlayerHandState,
                GameResult.from(command.gameResult()),
                command.finishedWithBlackjack(),
                command.createdAt());
        finishedGamePort.save(finishedGame);
    }
}
