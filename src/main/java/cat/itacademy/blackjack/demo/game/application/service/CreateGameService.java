package cat.itacademy.blackjack.demo.game.application.service;

import cat.itacademy.blackjack.demo.game.application.port.in.CreateGameUseCase;
import cat.itacademy.blackjack.demo.game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.game.application.service.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.demo.game.domain.GameState;
import cat.itacademy.blackjack.demo.game.domain.model.Dealer;
import cat.itacademy.blackjack.demo.game.domain.model.Deck;
import cat.itacademy.blackjack.demo.game.domain.model.Game;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.game.domain.model.UserPlayer;
import cat.itacademy.blackjack.demo.game.infrastructure.event.GameFinishedEvent;
import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.GameResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateGameService implements CreateGameUseCase {
    private final ApplicationEventPublisher eventPublisher;
    private final ActiveGamePort gamePort;
    private final ShuffleStrategy shuffleStrategy;

    @Override
    public GameResponseDto execute(String name) {
        UserPlayer player = UserPlayer.create(Name.of(name));
        Game game = Game.create(player, Dealer.create(), Deck.create());
        game.start(shuffleStrategy);

        if (game.getGameState() == GameState.OVER){
            eventPublisher.publishEvent(GameFinishedEvent.from(game) // crear bus que implemente ApplicationEventPublisher
            );
        } else {
            gamePort.saveGame(game);
        }
        return GameResponseDto.from(game);
    }
}
