package cat.itacademy.blackjack.demo.active_game.domain.event;

import cat.itacademy.blackjack.demo.finished_game.application.command.ProcessGameStatisticsCommand;
import cat.itacademy.blackjack.demo.finished_game.application.port.in.ProcessFinishedGameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameFinishedListener {
    private final ProcessFinishedGameUseCase processGameStatisticsUseCase;

    @Async
    @EventListener
    public void handleGameFinished(GameFinishedEvent event){
        processGameStatisticsUseCase.execute(ProcessGameStatisticsCommand.fromGameFinishedEvent(event));
    }
}
