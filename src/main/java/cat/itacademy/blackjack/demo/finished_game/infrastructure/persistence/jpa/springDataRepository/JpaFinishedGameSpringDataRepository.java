package cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.springDataRepository;

import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaFinishedGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaFinishedGameSpringDataRepository extends JpaRepository<JpaFinishedGameEntity, Long> {
    Optional<JpaFinishedGameEntity> findByGameId(UUID gameId);

    @Query (value = "SELECT fg FROM JpaFinishedGameEntity fg JOIN FETCH fg.player ORDER BY fg.finishedAt DESC")
    List<JpaFinishedGameEntity> findAllOrderedByFinishedAtDesc();

    @Query (value = "SELECT fg FROM JpaFinishedGameEntity fg JOIN FETCH fg.player ORDER BY fg.score DESC")
    List<JpaFinishedGameEntity> findAllOrderedByScoreDesc();

}
