package cat.itacademy.blackjack.demo.finished_game.domain.criteria;

import cat.itacademy.blackjack.demo.finished_game.domain.exception.InvalidFinishedGameSearch;

import java.util.Optional;

public record FinishedGameSortCriteria(
        FinishedGameSortType sortType,
        Optional<Long> playerId,
        Optional<String> playerName
) {
    public FinishedGameSortCriteria{
        if (playerId.isPresent() && playerName.isPresent()){
            throw new InvalidFinishedGameSearch("Cannot search by Player ID and Player Name simultaneously");
        }
    }

}
