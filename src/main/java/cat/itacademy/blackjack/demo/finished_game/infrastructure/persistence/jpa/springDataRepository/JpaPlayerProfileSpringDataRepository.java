package cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.springDataRepository;

import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaPlayerProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPlayerProfileSpringDataRepository extends JpaRepository<JpaPlayerProfileEntity, Long> {
    Optional<JpaPlayerProfileEntity> findByName(String name);
}
