package cat.itacademy.blackjack.demo.game.domain.value_object;

import cat.itacademy.blackjack.demo.common.domain.GameResult;

public record GameOutcome(
        GameResult result,
        boolean blackjack
) {
}
