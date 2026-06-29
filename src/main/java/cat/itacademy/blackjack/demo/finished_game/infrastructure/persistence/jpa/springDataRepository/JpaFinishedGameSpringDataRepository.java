package cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.springDataRepository;

import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaFinishedGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaFinishedGameSpringDataRepository extends JpaRepository<JpaFinishedGameEntity, Long>, JpaSpecificationExecutor<JpaFinishedGameEntity> {

    Optional<JpaFinishedGameEntity> findByGameId(UUID gameId);
}
