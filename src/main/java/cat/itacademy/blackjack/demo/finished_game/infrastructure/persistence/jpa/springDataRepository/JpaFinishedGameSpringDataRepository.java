package cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.springDataRepository;

import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaFinishedGameEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaFinishedGameSpringDataRepository extends JpaRepository<JpaFinishedGameEntity, Long> {
    Optional<JpaFinishedGameEntity> findByGameId(UUID gameId);

    @Query ("SELECT fg FROM JpaFinishedGameEntity fg JOIN FETCH fg.player")
    List<JpaFinishedGameEntity> findAllWithPlayer(Sort sort);

    @Query("SELECT fg FROM JpaFinishedGameEntity fg JOIN FETCH fg.player WHERE fg.player.id = :playerId")
    List<JpaFinishedGameEntity> findByPlayerId(@Param("playerId") Long playerId, Sort sort);

    @Query("SELECT fg FROM JpaFinishedGameEntity fg JOIN FETCH fg.player WHERE fg.player.name = :name")
    List<JpaFinishedGameEntity> findByPlayerName(@Param("name") String name, Sort sort);
}
