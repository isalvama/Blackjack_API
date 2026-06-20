package cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.mapper;

import cat.itacademy.blackjack.demo.game.domain.model.Hand;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.game.domain.model.UserPlayer;
import cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.document.PlayerDocument;
import org.springframework.stereotype.Component;

@Component
public class UserPlayerDocumentMapper {
        public static UserPlayer toModelEntity(PlayerDocument doc, String userName) {
            return UserPlayer.reconstitute(
                    Name.of(userName),
                    Hand.reconstitute(doc.getHand().stream().map(CardDocumentMapper::toModelEntity).toList(), doc.getTotalCardsValue())
            );
        }
}
