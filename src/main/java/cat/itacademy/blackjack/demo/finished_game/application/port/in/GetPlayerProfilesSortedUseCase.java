package cat.itacademy.blackjack.demo.finished_game.application.port.in;

import cat.itacademy.blackjack.demo.finished_game.domain.criteria.PlayerProfileSortType;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.web.PlayerProfileResponseDto;

import java.util.List;

public interface GetPlayerProfilesSortedUseCase {
    List<PlayerProfileResponseDto> execute(PlayerProfileSortType sort);
}
