package cat.itacademy.blackjack.demo.finished_game.application.port.in;

import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.PlayerProfileResponseDto;

public interface GetPlayerProfileUseCase {
    PlayerProfileResponseDto getPlayerById (Long id);
    PlayerProfileResponseDto getPlayerByName (String name);

}
