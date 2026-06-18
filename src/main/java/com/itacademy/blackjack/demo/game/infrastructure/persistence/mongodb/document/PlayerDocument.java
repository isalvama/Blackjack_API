package cat.itacademy.blackjack.game.infrastructure.persistence.mongodb.document;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
public class PlayerDocument {
        private Integer numberOfCards;
        private Integer totalCardsValue;
        private List<CardDocument> hand;

        public PlayerDocument(Integer numberOfCards, Integer totalCardsValue, List<CardDocument> hand) {
            this.numberOfCards = numberOfCards;
            this.totalCardsValue = totalCardsValue;
            this.hand = hand;
        }

    public List<CardDocument> getHand() {
        return List.copyOf(hand);
    }
}
