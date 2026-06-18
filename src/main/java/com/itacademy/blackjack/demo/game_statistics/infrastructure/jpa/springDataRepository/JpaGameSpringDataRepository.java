package cat.itacademy.blackjack.game_statistics.infrastructure.jpa.springDataRepository;

import cat.itacademy.blackjack.game_statistics.infrastructure.jpa.entity.JpaFinishedGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaGameSpringDataRepository extends JpaRepository<JpaFinishedGameEntity, Long> {
    Optional<JpaFinishedGameEntity> findByGameId(UUID gameId);
}
