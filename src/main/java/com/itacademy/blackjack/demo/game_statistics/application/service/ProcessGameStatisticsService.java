package cat.itacademy.blackjack.game_statistics.application.service;

import cat.itacademy.blackjack.game_statistics.application.command.ProcessGameStatisticsCommand;
import cat.itacademy.blackjack.game_statistics.application.port.in.ProcessGameStatisticsUseCase;
import cat.itacademy.blackjack.common.domain.GameResult;
import cat.itacademy.blackjack.game_statistics.application.port.out.FinishedGamePort;
import cat.itacademy.blackjack.game_statistics.application.port.out.PlayerProfilePort;
import cat.itacademy.blackjack.game_statistics.domain.model.FinishedGame;
import cat.itacademy.blackjack.game_statistics.domain.model.PlayerProfile;
import cat.itacademy.blackjack.common.domain.value_object.GameId;
import cat.itacademy.blackjack.game_statistics.domain.value_object.HandState;
import cat.itacademy.blackjack.common.domain.value_object.Name;
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

        playerProfile.updateProfileWithNewGame();
        playerProfilePort.save(playerProfile);

        GameId id = GameId.fromUUID(command.gameId());
        HandState userPlayerHandState = HandState.create(command.numberOfRequestedCardsByUser(), command.numberOfRequestedCardsByUser());
        HandState dealerPlayerHandState = HandState.create(command.numberOfRequestedCardsByDealer(), command.numberOfRequestedCardsByDealer());
        Integer gameNumber = finishedGamePort.countGames();
        FinishedGame finishedGame = FinishedGame.create(
                id,
                gameNumber,
                userPlayerHandState,
                dealerPlayerHandState,
                GameResult.from(command.gameResult()),
                command.finishedWithBlackjack(),
                command.createdAt(),
                command.finishedAt());

        finishedGamePort.save(finishedGame);
    }
}
