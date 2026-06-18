package cat.itacademy.blackjack.game.infrastructure.persistence.mongodb.mapper;

import cat.itacademy.blackjack.game.domain.GameState;
import cat.itacademy.blackjack.game.domain.model.Deck;
import cat.itacademy.blackjack.game.domain.model.Game;
import cat.itacademy.blackjack.common.domain.value_object.GameId;
import cat.itacademy.blackjack.game.infrastructure.persistence.mongodb.document.GameDocument;
import org.springframework.stereotype.Component;

@Component
public class GameDocumentMapper {
    private GameDocumentMapper() {
    }

    public static Game toModelEntity(GameDocument doc) {
        return new Game(
                GameId.fromString(doc.getId()),
                GameState.valueOf(doc.getGameState()),
                UserPlayerDocumentMapper.toModelEntity(doc.getUserPlayerInfo(), doc.getUserName()),
                DealerDocumentMapper.toModelEntity(doc.getDealerInfo()),
               Deck.from(doc.getDeck()
                        .stream()
                        .map(CardDocumentMapper::toModelEntity)
                        .toList()),
                doc.getCreatedAt(),
                doc.getLastTimePlayedAt(),
                null
        );
    }

public static GameDocument toDocument(Game entity) {
        return new GameDocument(
                entity.getId().toString(),
                entity.getCreatedAt(),
                entity.getLastTimePlayedAt(),
                entity.getUserPlayer().getName().value(),
                PlayerDocumentMapper.toDocument(entity.getUserPlayer()),
                PlayerDocumentMapper.toDocument(entity.getDealer()),
                entity.getGameState().name(),
                entity.getDeck().getCards().stream().map(CardDocumentMapper::toDocument).toList()
        );
    }
}
