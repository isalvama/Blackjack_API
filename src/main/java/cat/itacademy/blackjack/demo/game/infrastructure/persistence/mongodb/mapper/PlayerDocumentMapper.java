package cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.mapper;

import cat.itacademy.blackjack.demo.game.domain.model.Player;
import cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.document.PlayerDocument;
import org.springframework.stereotype.Component;

@Component
public class PlayerDocumentMapper {
    public static PlayerDocument toDocument(Player entity) {
        return new PlayerDocument(
                entity.getHand().getCards().size(),
                entity.getHand().getTotalValue(),
                entity.getHand().getCards().stream().map(CardDocumentMapper::toDocument).toList()
        );
    }

}
