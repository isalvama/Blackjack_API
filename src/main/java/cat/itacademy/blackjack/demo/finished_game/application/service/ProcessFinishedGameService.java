package cat.itacademy.blackjack.demo.finished_game.application.service;

import cat.itacademy.blackjack.demo.finished_game.application.command.ProcessGameStatisticsCommand;
import cat.itacademy.blackjack.demo.finished_game.application.exception.GameAlreadyExistsException;
import cat.itacademy.blackjack.demo.finished_game.application.mapper.ProcessFinishedGameMapper;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.ProcessFinishedGameUseCase;
import cat.itacademy.blackjack.demo.finished_game.application.port.out.FinishedGamePort;
import cat.itacademy.blackjack.demo.finished_game.application.port.out.PlayerProfilePort;
import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;
import cat.itacademy.blackjack.demo.finished_game.domain.model.PlayerProfile;
import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessFinishedGameService implements ProcessFinishedGameUseCase {
    private final PlayerProfilePort playerProfilePort;
    private final FinishedGamePort finishedGamePort;
    private final ProcessFinishedGameMapper processFinishedGameMapper;

    @Transactional
    @Override
    public void execute(ProcessGameStatisticsCommand command) {

        if (finishedGamePort.findById(GameId.fromUUID(command.gameId())).isPresent()){
            throw new GameAlreadyExistsException(command.gameId().toString(), "A game with id " + command.gameId() + " has already been processed.");
        }

        PlayerProfile playerProfile = playerProfilePort.findByName(command.playerName())
                .orElseGet(() -> {
                    Name playerName = Name.of(command.playerName());
                    return PlayerProfile.create(playerName);
                });

        FinishedGame finishedGame = processFinishedGameMapper.toDomain(command);

        playerProfile.updateProfileWithNewGame(finishedGame.getGameResult(), finishedGame.getScore());
        PlayerProfile savedProfileUpdated = playerProfilePort.save(playerProfile);

        finishedGame.addPlayerProfileInfo(savedProfileUpdated.getId(), savedProfileUpdated.getName());
        finishedGamePort.save(finishedGame);
    }
}
