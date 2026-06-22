package cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.repository;

import cat.itacademy.blackjack.demo.finished_game.application.port.out.PlayerProfilePort;
import cat.itacademy.blackjack.demo.finished_game.domain.model.PlayerProfile;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaPlayerProfileEntity;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.mapper.PlayerProfileMapper;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.springDataRepository.JpaPlayerProfileSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaPlayerProfileRepository implements PlayerProfilePort {

    private final PlayerProfileMapper playerProfileMapper;
    private final JpaPlayerProfileSpringDataRepository jpaPlayerProfileSpringDataRepository;

    @Override
    public Optional<PlayerProfile> findByName(String name) {
        return jpaPlayerProfileSpringDataRepository.findByName(name).map(playerProfileMapper::toDomain);
    }

    @Override
    public PlayerProfile save(PlayerProfile playerProfile) {
        JpaPlayerProfileEntity jpaPlayerProfileEntity = jpaPlayerProfileSpringDataRepository.save(playerProfileMapper.toEntity(playerProfile));
        return playerProfileMapper.toDomain(jpaPlayerProfileEntity);
    }
}
