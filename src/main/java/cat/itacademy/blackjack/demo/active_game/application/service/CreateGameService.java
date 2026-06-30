package cat.itacademy.blackjack.demo.active_game.application.service;

import cat.itacademy.blackjack.demo.active_game.application.port.in.CreateGameUseCase;
import cat.itacademy.blackjack.demo.active_game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.active_game.domain.shuffle_strategy.ShuffleStrategy;
import cat.itacademy.blackjack.demo.active_game.domain.GameState;
import cat.itacademy.blackjack.demo.active_game.domain.event.GameFinishedEventPublisher;
import cat.itacademy.blackjack.demo.active_game.domain.model.Dealer;
import cat.itacademy.blackjack.demo.active_game.domain.model.Deck;
import cat.itacademy.blackjack.demo.active_game.domain.model.Game;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.active_game.domain.model.UserPlayer;
import cat.itacademy.blackjack.demo.active_game.domain.event.GameFinishedEvent;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.ActiveGameResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateGameService implements CreateGameUseCase {
    private final GameFinishedEventPublisher eventPublisher;
    private final ActiveGamePort gamePort;
    private final ShuffleStrategy shuffleStrategy;

    @Override
    public ActiveGameResponseDto execute(String name) {
        UserPlayer player = UserPlayer.create(Name.of(name));
        Game game = Game.create(player, Dealer.create(), Deck.create());
        game.start(shuffleStrategy);
        game.updateAuditInfo(LocalDateTime.now(), LocalDateTime.now());

        if (game.getGameState() == GameState.OVER){
            gamePort.deleteActiveGame(game.getId());
            eventPublisher.publishEvent(GameFinishedEvent.from(game));
        } else {
            game = gamePort.saveActiveGame(game);
        }
        return ActiveGameResponseDto.from(game);
    }
}
