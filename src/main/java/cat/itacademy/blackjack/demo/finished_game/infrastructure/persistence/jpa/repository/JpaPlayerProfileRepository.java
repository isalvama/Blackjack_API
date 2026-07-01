package cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.repository;

import cat.itacademy.blackjack.demo.finished_game.application.port.out.PlayerProfilePort;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.PlayerProfileSortType;
import cat.itacademy.blackjack.demo.finished_game.domain.model.PlayerProfile;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaPlayerProfileEntity;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.mapper.PlayerProfileMapper;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.springDataRepository.JpaPlayerProfileSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public Optional<PlayerProfile> findById(Long id) {
        return jpaPlayerProfileSpringDataRepository.findById(id).map(playerProfileMapper::toDomain);
    }

    @Override
    public PlayerProfile save(PlayerProfile playerProfile) {
        JpaPlayerProfileEntity jpaPlayerProfileEntity;
        if (playerProfile.getId() == null) {
            jpaPlayerProfileEntity = playerProfileMapper.toEntity(playerProfile);
        } else {
            jpaPlayerProfileEntity = jpaPlayerProfileSpringDataRepository.findById(playerProfile.getId())
                    .map(existing -> {
                        existing.updateProfile(
                                playerProfile.getNumberOfGamesPlayed(),
                                playerProfile.getNumberOfGamesWon(),
                                playerProfile.getScore()
                        );
                        return existing;
                    })
                    .orElseGet(() -> playerProfileMapper.toEntity(playerProfile));
        }
        JpaPlayerProfileEntity savedEntity = jpaPlayerProfileSpringDataRepository.save(jpaPlayerProfileEntity);
        return playerProfileMapper.toDomain(savedEntity);
    }

    @Override
    public List<PlayerProfile> findAllSorted(PlayerProfileSortType sortType) {
        Sort sort = Sort.by(
                Sort.Direction.valueOf(sortType.getOrderType().name()),
                sortType.getEntityProperty()
        );
        List<JpaPlayerProfileEntity> jpaPlayerProfileEntity = jpaPlayerProfileSpringDataRepository.findAll(sort);
        return playerProfileMapper.toDomain(jpaPlayerProfileEntity);
    }
}
