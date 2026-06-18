package cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.mapper;

import cat.itacademy.blackjack.demo.game.domain.model.Dealer;
import cat.itacademy.blackjack.demo.game.domain.model.Hand;
import cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.document.PlayerDocument;
import org.springframework.stereotype.Component;

    @Component
    public class DealerDocumentMapper {
        public static Dealer toModelEntity(PlayerDocument doc) {
            return new Dealer(
                    new Hand(doc.getHand().stream().map(CardDocumentMapper::toModelEntity).toList(), doc.getTotalCardsValue())
            );
        }
    }
