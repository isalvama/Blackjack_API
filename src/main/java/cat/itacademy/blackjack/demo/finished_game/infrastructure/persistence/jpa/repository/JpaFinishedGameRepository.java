package cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.repository;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.finished_game.application.port.out.FinishedGamePort;
import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaFinishedGameEntity;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.mapper.FinishedGameMapper;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.springDataRepository.JpaFinishedGameSpringDataRepository;
import lombok.RequiredArgsConstructor;
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
    public List<FinishedGame> getAllOrderedByFinishedAtDesc(){
        List<JpaFinishedGameEntity> finishedGames = jpaGameSpringDataRepository.findAllOrderedByFinishedAtDesc();
        return finishedGames.stream().map(finishedGameMapper::toDomain).toList();
    }

    @Override
    public List<FinishedGame> getAllOrderedByScoreDesc(){
        List<JpaFinishedGameEntity> finishedGames = jpaGameSpringDataRepository.findAllOrderedByFinishedAtDesc();
        return finishedGames.stream().map(finishedGameMapper::toDomain).toList();
    }
}
