package cat.itacademy.blackjack.demo.finished_game.application.port.in;

import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.player_profiles.dto.PlayerProfileResponseDto;

public interface GetPlayerProfileUseCase {
    PlayerProfileResponseDto getPlayerById (Long id);
    PlayerProfileResponseDto getPlayerByName (String name);

}
