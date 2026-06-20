package cat.itacademy.blackjack.demo.game.domain.event;

import cat.itacademy.blackjack.demo.game_statistics.application.command.ProcessGameStatisticsCommand;
import cat.itacademy.blackjack.demo.game_statistics.application.port.in.ProcessGameStatisticsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameFinishedListener {
    private final ProcessGameStatisticsUseCase processGameStatisticsUseCase;

    @Async
    @EventListener
    public void handleGameFinished(GameFinishedEvent event){
        processGameStatisticsUseCase.execute(ProcessGameStatisticsCommand.fromGameFinishedEvent(event));
    }
}
