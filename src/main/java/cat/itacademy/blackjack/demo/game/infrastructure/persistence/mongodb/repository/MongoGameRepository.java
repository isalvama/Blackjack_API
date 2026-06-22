package cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.repository;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.demo.game.domain.model.Game;
import cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.springdatarepository.MongoGameSpringDataRepository;
import cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.document.GameDocument;
import cat.itacademy.blackjack.demo.game.infrastructure.persistence.mongodb.mapper.GameDocumentMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Profile("mongodb")
@Repository
public class MongoGameRepository implements ActiveGamePort {
    private final MongoGameSpringDataRepository mongoGameSpringDataRepository;

    public MongoGameRepository(MongoGameSpringDataRepository mongoGameSpringDataRepository) {
        this.mongoGameSpringDataRepository = mongoGameSpringDataRepository;
    }

    @Override
    public Game saveActiveGame(Game game) {
        GameDocument gameDocument = GameDocumentMapper.toDocument(game);
        GameDocument docCreated = mongoGameSpringDataRepository.save(gameDocument);
       return GameDocumentMapper.toModelEntity(docCreated);
    }

    @Override
    public Optional<Game> getActiveGame(GameId id) {
        return mongoGameSpringDataRepository.findById(id.toString())
                .map(GameDocumentMapper::toModelEntity);
    }

    @Override
    public List<Game> getAllActiveGames() {
        List<GameDocument> gameDocuments = mongoGameSpringDataRepository.findAll();
        if (!gameDocuments.isEmpty()){
           return gameDocuments.stream().map(GameDocumentMapper::toModelEntity).toList();
        }
        return List.of();
    }

    @Override
    public void deleteActiveGame(GameId id) {
        mongoGameSpringDataRepository.deleteById(id.toString());
    }
}
