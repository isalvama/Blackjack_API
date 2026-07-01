package cat.itacademy.blackjack.demo.finished_game.application.port.in;

import cat.itacademy.blackjack.demo.finished_game.application.command.ProcessGameStatisticsCommand;

public interface ProcessFinishedGameUseCase {
    void execute(ProcessGameStatisticsCommand registerFinishedGameCommand);
}
