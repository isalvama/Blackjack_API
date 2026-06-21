package cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.repository;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.game.domain.model.Game;
import cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.springdatarepository.MongoGameSpringDataRepository;
import cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.document.GameDocument;
import cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.mapper.GameDocumentMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Profile("mongodb")
@Repository
public class MongoGameRepository implements ActiveGamePort {
    private final MongoGameSpringDataRepository mongoGameSpringDataRepository;

    public MongoGameRepository(MongoGameSpringDataRepository mongoGameSpringDataRepository) {
        this.mongoGameSpringDataRepository = mongoGameSpringDataRepository;
    }

    @Override
    public Game saveGame(Game game) {
        GameDocument gameDocument = GameDocumentMapper.toDocument(game);
        if (gameDocument.getCreatedAt() == null){
            gameDocument.setCreatedAt(LocalDateTime.now());
        }
        gameDocument.setLastTimePlayedAt(LocalDateTime.now());
        GameDocument docCreated = mongoGameSpringDataRepository.insert(gameDocument);
       return GameDocumentMapper.toModelEntity(docCreated);
    }

    @Override
    public Optional<Game> getGame(GameId id) {
        return mongoGameSpringDataRepository.findById(id.toString())
                .map(GameDocumentMapper::toModelEntity);
    }
}
