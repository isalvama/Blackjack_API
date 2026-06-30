package cat.itacademy.blackjack.demo.active_game.application.service;

import cat.itacademy.blackjack.demo.active_game.application.port.in.GetAllActiveGamesUseCase;
import cat.itacademy.blackjack.demo.active_game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.active_game.domain.model.Game;
import cat.itacademy.blackjack.demo.active_game.infrastructure.web.dto.ActiveGameResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllActiveGamesService implements GetAllActiveGamesUseCase {
    private final ActiveGamePort activeGamePort;
    @Override
    public List<ActiveGameResponseDto> execute() {
        List<Game> games = activeGamePort.getAllActiveGames();
        if (!games.isEmpty()){
            return games.stream().map(ActiveGameResponseDto::from).toList();
        }
        return List.of();
    }
}
