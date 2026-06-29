package cat.itacademy.blackjack.demo.active_game.infrastructure.persistence.mongodb.mapper;

import cat.itacademy.blackjack.demo.active_game.domain.model.Player;
import cat.itacademy.blackjack.demo.active_game.infrastructure.persistence.mongodb.document.PlayerDocument;
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
