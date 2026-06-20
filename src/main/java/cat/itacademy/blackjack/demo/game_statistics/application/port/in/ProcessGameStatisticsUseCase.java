package cat.itacademy.blackjack.demo.game_statistics.application.port.in;

import cat.itacademy.blackjack.demo.game_statistics.application.command.ProcessGameStatisticsCommand;

public interface ProcessGameStatisticsUseCase {
    void execute(ProcessGameStatisticsCommand registerFinishedGameCommand);
}
