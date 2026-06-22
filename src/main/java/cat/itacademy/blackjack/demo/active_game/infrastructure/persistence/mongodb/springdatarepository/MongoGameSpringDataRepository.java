package cat.itacademy.blackjack.demo.active_game.infrastructure.persistence.mongodb.springdatarepository;

import cat.itacademy.blackjack.demo.active_game.infrastructure.persistence.mongodb.document.GameDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MongoGameSpringDataRepository extends MongoRepository<GameDocument, String> {

}
