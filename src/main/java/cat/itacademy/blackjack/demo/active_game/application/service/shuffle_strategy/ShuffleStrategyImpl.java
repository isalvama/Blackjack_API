package cat.itacademy.blackjack.demo.active_game.application.service.shuffle_strategy;
import cat.itacademy.blackjack.demo.active_game.domain.value_object.Card;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ShuffleStrategyImpl implements ShuffleStrategy{

    @Override
    public List<Card> shuffle(List<Card> cards) {
        Collections.shuffle(cards);
        return cards;
    }
}
