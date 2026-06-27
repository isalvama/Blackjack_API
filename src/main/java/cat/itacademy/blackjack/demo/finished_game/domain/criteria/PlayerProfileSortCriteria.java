package cat.itacademy.blackjack.demo.finished_game.domain.criteria;

import java.util.Optional;

public record PlayerProfileSortCriteria (
        FinishedGameSortType sortType,
        Optional<String> playerName
) {
}
