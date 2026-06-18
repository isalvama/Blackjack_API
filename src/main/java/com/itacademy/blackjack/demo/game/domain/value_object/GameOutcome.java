package cat.itacademy.blackjack.game.domain.value_object;

import cat.itacademy.blackjack.common.domain.GameResult;

import java.time.LocalDateTime;

public record GameOutcome(
        GameResult result,
        boolean blackjack,
        LocalDateTime finishedAt
) {
}
