package cat.itacademy.blackjack.game.application.service.shuffle_strategy;
import cat.itacademy.blackjack.game.domain.value_object.Card;

import java.util.List;

public interface ShuffleStrategy {

    List<Card> shuffle(List<Card> cards);
}
