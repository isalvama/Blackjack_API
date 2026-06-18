package cat.itacademy.blackjack.game.infrastructure.persistence.mongodb.repository;

import cat.itacademy.blackjack.game.application.port.out.ActiveGamePort;
import cat.itacademy.blackjack.game.domain.model.Game;
import cat.itacademy.blackjack.game.infrastructure.persistence.mongodb.springdatarepository.MongoGameSpringDataRepository;
import cat.itacademy.blackjack.game.infrastructure.persistence.mongodb.document.GameDocument;
import cat.itacademy.blackjack.game.infrastructure.persistence.mongodb.mapper.GameDocumentMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Profile("mongodb")
@Repository
public class MongoGameRepository implements ActiveGamePort {
    private final MongoGameSpringDataRepository mongoGameSpringDataRepository;

    public MongoGameRepository(MongoGameSpringDataRepository mongoGameSpringDataRepository) {
        this.mongoGameSpringDataRepository = mongoGameSpringDataRepository;
    }

    @Override
    public Game saveGame(Game game) {
        GameDocument created = mongoGameSpringDataRepository.insert(GameDocumentMapper.toDocument(game));
        return GameDocumentMapper.toModelEntity(created);
    }
}
