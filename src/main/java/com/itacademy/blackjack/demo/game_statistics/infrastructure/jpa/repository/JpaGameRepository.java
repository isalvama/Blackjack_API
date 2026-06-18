package cat.itacademy.blackjack.game_statistics.infrastructure.jpa.repository;

import cat.itacademy.blackjack.common.domain.value_object.GameId;
import cat.itacademy.blackjack.game_statistics.application.port.out.FinishedGamePort;
import cat.itacademy.blackjack.game_statistics.domain.model.FinishedGame;
import cat.itacademy.blackjack.game_statistics.infrastructure.jpa.entity.JpaFinishedGameEntity;
import cat.itacademy.blackjack.game_statistics.infrastructure.jpa.mapper.FinishedGameMapper;
import cat.itacademy.blackjack.game_statistics.infrastructure.jpa.springDataRepository.JpaGameSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaGameRepository implements FinishedGamePort {

    private final JpaGameSpringDataRepository jpaGameSpringDataRepository;
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
}
