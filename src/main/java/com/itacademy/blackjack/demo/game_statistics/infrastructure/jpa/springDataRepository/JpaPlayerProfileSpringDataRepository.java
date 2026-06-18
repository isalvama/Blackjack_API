package cat.itacademy.blackjack.game_statistics.infrastructure.jpa.springDataRepository;

import cat.itacademy.blackjack.game_statistics.infrastructure.jpa.entity.JpaPlayerProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPlayerProfileSpringDataRepository extends JpaRepository<JpaPlayerProfileEntity, Long> {
    Optional<JpaPlayerProfileEntity> findByName(String name);
}
