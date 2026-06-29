package cat.itacademy.blackjack.demo.active_game.infrastructure.persistence.mongodb.mapper;

import cat.itacademy.blackjack.demo.active_game.domain.model.Dealer;
import cat.itacademy.blackjack.demo.active_game.domain.model.Hand;
import cat.itacademy.blackjack.demo.active_game.infrastructure.persistence.mongodb.document.PlayerDocument;
import org.springframework.stereotype.Component;

    @Component
    public class DealerDocumentMapper {
        public static Dealer toModelEntity(PlayerDocument doc) {
            return Dealer.reconstitute(
                    Hand.reconstitute(doc.getHand().stream().map(CardDocumentMapper::toModelEntity).toList(), doc.getTotalCardsValue())
            );
        }
    }
