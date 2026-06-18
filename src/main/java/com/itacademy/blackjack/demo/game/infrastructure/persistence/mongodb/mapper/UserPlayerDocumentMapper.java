package cat.itacademy.blackjack.game.infrastructure.persistence.mongodb.mapper;

import cat.itacademy.blackjack.game.domain.model.Hand;
import cat.itacademy.blackjack.game.domain.model.UserPlayer;
import cat.itacademy.blackjack.common.domain.value_object.Name;
import cat.itacademy.blackjack.game.infrastructure.persistence.mongodb.document.PlayerDocument;
import org.springframework.stereotype.Component;

@Component
public class UserPlayerDocumentMapper {
        public static UserPlayer toModelEntity(PlayerDocument doc, String userName) {
            return new UserPlayer(
                    Name.of(userName),
                    new Hand(doc.getHand().stream().map(CardDocumentMapper::toModelEntity).toList(), doc.getTotalCardsValue())
            );
        }
}
