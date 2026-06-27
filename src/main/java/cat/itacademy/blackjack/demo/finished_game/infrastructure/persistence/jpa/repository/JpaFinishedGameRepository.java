package cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.repository;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.finished_game.application.port.out.FinishedGamePort;
import cat.itacademy.blackjack.demo.finished_game.domain.criteria.FinishedGameSortCriteria;
import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaFinishedGameEntity;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.mapper.FinishedGameMapper;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.springDataRepository.JpaFinishedGameSpringDataRepository;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

        Specification<JpaFinishedGameEntity> fetchSpec = (root, query, cb) -> {
            if (query.getResultType() != Long.class) {
                root.fetch("player", JoinType.INNER);
            }
            return cb.conjunction(); // "WHERE 1=1" (no filtra nada aún)
        };

        List<Specification<JpaFinishedGameEntity>> filters = new ArrayList<>();
        filters.add(fetchSpec);

        criteria.playerId().ifPresent(id ->
                filters.add((root, query, cb) -> cb.equal(root.get("player").get("id"), id))
        );

        criteria.playerName().ifPresent(name ->
                filters.add((root, query, cb) -> cb.equal(root.get("player").get("name"), name))
        );

        Specification<JpaFinishedGameEntity> finalSpec = Specification.allOf(filters);

        List<JpaFinishedGameEntity> entities = jpaGameSpringDataRepository.findAll(finalSpec, sort);

        return finishedGameMapper.toDomain(entities);
    }
}
