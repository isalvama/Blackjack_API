package cat.itacademy.blackjack.demo.active_game.domain.shuffle_strategy;
import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;

import java.util.List;

public interface ShuffleStrategy {

    List<Card> shuffle(List<Card> cards);
}
