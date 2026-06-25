package cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.repository;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.finished_game.application.port.out.FinishedGamePort;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortCriteria;
import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaFinishedGameEntity;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.mapper.FinishedGameMapper;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.springDataRepository.JpaFinishedGameSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaFinishedGameRepository implements FinishedGamePort {

    private final JpaFinishedGameSpringDataRepository jpaGameSpringDataRepository;
    private final FinishedGameMapper finishedGameMapper;

    @Override
    public Optional<FinishedGame> findById(GameId id){
        Optional<JpaFinishedGameEntity> entity = jpaGameSpringDataRepository.findByGameId(id.value());
        return entity.map(finishedGameMapper::toDomain);
    }

    @Override
    public void save(FinishedGame game) {
        jpaGameSpringDataRepository.save(finishedGameMapper.toEntity(game));
    }

    @Override
    public Integer countGames() {
        return Math.toIntExact(jpaGameSpringDataRepository.count());
    }

    @Override
    public List<FinishedGame> getByCriteria(FinishedGameSortCriteria criteria) {
        Sort sort = Sort.by(
                Sort.Direction.valueOf(criteria.sortType().getOrderType().name()),
                criteria.sortType().getEntityProperty()
        );

        List<JpaFinishedGameEntity> entities = criteria.playerId().map(p -> jpaGameSpringDataRepository.findByPlayerId(p, sort))
                .or(() -> criteria.playerName().map(p -> jpaGameSpringDataRepository.findByPlayerName(p, sort)))
                .orElseGet(() -> jpaGameSpringDataRepository.findAllWithPlayer(sort));

        return finishedGameMapper.toDomain(entities);

    }
}
