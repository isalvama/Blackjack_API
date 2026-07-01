package cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.mapper;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.finished_game.domain.model.FinishedGame;
import cat.itacademy.blackjack.demo.finished_game.domain.value_object.HandState;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaFinishedGameEntity;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaPlayerProfileEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FinishedGameMapper {

    public JpaFinishedGameEntity toEntity(FinishedGame finishedGame) {

        JpaPlayerProfileEntity playerProxy = new JpaPlayerProfileEntity();
        playerProxy.setId(finishedGame.getPlayerId());
        return new JpaFinishedGameEntity(
                finishedGame.getId() != null ? finishedGame.getId() : null,
                finishedGame.getGameId().value(),
                playerProxy,
                finishedGame.getPlayerHandState().numberOfRequestedCards(),
                finishedGame.getDealerHandState().numberOfRequestedCards(),
                finishedGame.getPlayerHandState().totalCardsValue(),
                finishedGame.getDealerHandState().totalCardsValue(),
                finishedGame.getGameResult(),
                finishedGame.getFinishedWithBlackjack(),
                finishedGame.getScore(),
                finishedGame.getCreatedAt(),
                finishedGame.getFinishedAt()
        );
    }

    public FinishedGame toDomain(JpaFinishedGameEntity finishedGameEntity) {
        return FinishedGame.reconstitute(
                finishedGameEntity.getId(),
                GameId.fromUUID(finishedGameEntity.getGameId()),
                finishedGameEntity.getPlayer().getId(),
                Name.of(finishedGameEntity.getPlayer().getName()),
                HandState.create(finishedGameEntity.getPlayerNumberOfCards(), finishedGameEntity.getTotalCardsValuePlayer()),
                HandState.create(finishedGameEntity.getDealerNumberOfCards(), finishedGameEntity.getTotalCardsValueDealer()),
                finishedGameEntity.getGameResult(),
                finishedGameEntity.getFinishedWithBlackjack(),
                finishedGameEntity.getCreatedAt(),
                finishedGameEntity.getFinishedAt(),
                finishedGameEntity.getScore()
        );
    }

    public List<FinishedGame> toDomain(List<JpaFinishedGameEntity> finishedGameEntities) {
        return finishedGameEntities.stream().map(this::toDomain).toList();
    }
}
