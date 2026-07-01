package cat.itacademy.blackjack.demo.active_game.infrastructure.event;

import cat.itacademy.blackjack.demo.active_game.domain.event.GameFinishedEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class GameEventPublisherAdapter implements GameFinishedEventPublisher {
    private final ApplicationEventPublisher publisher;

    public GameEventPublisherAdapter(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publishEvent(Object event) {
        publisher.publishEvent(event);
    }
}
