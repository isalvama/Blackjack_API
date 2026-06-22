package cat.itacademy.blackjack.demo.game.application.service;

import cat.itacademy.blackjack.demo.game.application.port.in.GetAllActiveGamesUseCase;
import cat.itacademy.blackjack.demo.game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.game.domain.model.Game;
import cat.itacademy.blackjack.demo.game.infrastructure.web.dto.GameResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllActiveGamesService implements GetAllActiveGamesUseCase {
    private final ActiveGamePort activeGamePort;
    @Override
    public List<GameResponseDto> execute() {
        List<Game> games = activeGamePort.getAllActiveGames();
        if (!games.isEmpty()){
            return games.stream().map(GameResponseDto::from).toList();
        }
        return List.of();
    }
}
