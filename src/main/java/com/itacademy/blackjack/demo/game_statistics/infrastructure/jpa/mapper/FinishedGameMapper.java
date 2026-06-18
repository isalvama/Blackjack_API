package cat.itacademy.blackjack.game_statistics.infrastructure.jpa.mapper;

import cat.itacademy.blackjack.common.domain.value_object.GameId;
import cat.itacademy.blackjack.game_statistics.domain.model.FinishedGame;
import cat.itacademy.blackjack.game_statistics.domain.value_object.HandState;
import cat.itacademy.blackjack.game_statistics.infrastructure.jpa.entity.JpaFinishedGameEntity;
import org.springframework.stereotype.Component;

@Component
public class FinishedGameMapper {

    public JpaFinishedGameEntity toEntity(FinishedGame finishedGame){
        return JpaFinishedGameEntity.builder()
                .id(null)
                .gameId(finishedGame.getId().value())
                .gameNumber(finishedGame.getGameNumber())
                .userPlayer(null)
                .numberOfRequestedCardsByUserPlayer(finishedGame.getUserPlayerHandState().numberOfRequestedCards())
                .numberOfRequestedCardsByDealer(finishedGame.getDealerHandState().numberOfRequestedCards())
                .totalCardsValueUserPlayer(finishedGame.getUserPlayerHandState().totalCardsValue())
                .totalCardsValueDealer(finishedGame.getDealerHandState().totalCardsValue())
                .gameResult(finishedGame.getGameResult())
                .finishedWithBlackjack(finishedGame.getFinishedWithBlackjack())
                .createdAt(finishedGame.getCreatedAt())
                .finishedAt(finishedGame.getFinishedAt())
                .build();
    }

    public FinishedGame toDomain (JpaFinishedGameEntity finishedGameEntity){
        return FinishedGame.reconstitute(
                GameId.fromUUID(finishedGameEntity.getGameId()),
                finishedGameEntity.getUserPlayer().getId(),
                finishedGameEntity.getGameNumber(),
                HandState.create(finishedGameEntity.getNumberOfRequestedCardsByUserPlayer(), finishedGameEntity.getTotalCardsValueUserPlayer()),
                HandState.create(finishedGameEntity.getNumberOfRequestedCardsByDealer(), finishedGameEntity.getTotalCardsValueDealer()),
                finishedGameEntity.getGameResult(),
                finishedGameEntity.getFinishedWithBlackjack(),
                finishedGameEntity.getCreatedAt(),
                finishedGameEntity.getFinishedAt()
        );
    }
}
