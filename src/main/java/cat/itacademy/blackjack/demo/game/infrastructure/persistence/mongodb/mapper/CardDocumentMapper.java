package cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.mapper;

import cat.itacademy.blackjack.demo.game.domain.CardNumber;
import cat.itacademy.blackjack.demo.game.domain.Suit;
import cat.itacademy.blackjack.demo.game.domain.value_object.Card;
import cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.document.CardDocument;
import org.springframework.stereotype.Component;

@Component
public class CardDocumentMapper {
    public static Card toModelEntity(CardDocument doc) {
        return new Card(
                CardNumber.valueOf(doc.getNumber()),
                Suit.valueOf(doc.getSuit())
        );
    }

    public static CardDocument toDocument(Card entity) {
        return new CardDocument(
                entity.cardNumber().name(),
                entity.suit().name()
        );
    }
}
