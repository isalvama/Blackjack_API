package cat.itacademy.blackjack.demo.game_statistics.infrastructure.jpa.mapper;

import cat.itacademy.blackjack.demo.common.domain.value_object.GameId;
import cat.itacademy.blackjack.demo.game_statistics.domain.model.FinishedGame;
import cat.itacademy.blackjack.demo.game_statistics.domain.value_object.HandState;
import cat.itacademy.blackjack.demo.game_statistics.infrastructure.jpa.entity.JpaFinishedGameEntity;
import org.springframework.stereotype.Component;

@Component
public class FinishedGameMapper {

    public JpaFinishedGameEntity toEntity(FinishedGame finishedGame){
        return JpaFinishedGameEntity.builder()
                .id(null)
                .gameId(finishedGame.getGameId().value())
                .player(null)
                .playerNumberOfCards(finishedGame.getPlayerHandState().numberOfRequestedCards())
                .dealerNumberOfCards(finishedGame.getDealerHandState().numberOfRequestedCards())
                .totalCardsValuePlayer(finishedGame.getPlayerHandState().totalCardsValue())
                .totalCardsValueDealer(finishedGame.getDealerHandState().totalCardsValue())
                .gameResult(finishedGame.getGameResult())
                .finishedWithBlackjack(finishedGame.getFinishedWithBlackjack())
                .createdAt(finishedGame.getCreatedAt())
                .finishedAt(finishedGame.getFinishedAt())
                .build();
    }

    public FinishedGame toDomain (JpaFinishedGameEntity finishedGameEntity){
        return FinishedGame.reconstitute(
                finishedGameEntity.getId(),
                GameId.fromUUID(finishedGameEntity.getGameId()),
                finishedGameEntity.getPlayer().getId(),
                HandState.create(finishedGameEntity.getPlayerNumberOfCards(), finishedGameEntity.getTotalCardsValuePlayer()),
                HandState.create(finishedGameEntity.getDealerNumberOfCards(), finishedGameEntity.getTotalCardsValueDealer()),
                finishedGameEntity.getGameResult(),
                finishedGameEntity.getFinishedWithBlackjack(),
                finishedGameEntity.getCreatedAt(),
                finishedGameEntity.getFinishedAt()
        );
    }
}
