package cat.itacademy.blackjack.demo.finished_game.domain.criteria;

import cat.itacademy.blackjack.demo.finished_game.application.exception.InvalidFinishedGameSearchException;

import java.util.Optional;

public record FinishedGameSortCriteria(
        FinishedGameSortType sortType,
        Optional<Long> playerId,
        Optional<String> playerName
) {
    public FinishedGameSortCriteria{
        if (playerId.isPresent() && playerName.isPresent()){
            throw new InvalidFinishedGameSearchException("Cannot search by Player ID and Player Name simultaneously");
        }
    }

}
