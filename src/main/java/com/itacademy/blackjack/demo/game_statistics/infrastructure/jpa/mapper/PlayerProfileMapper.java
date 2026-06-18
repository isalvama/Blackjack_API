package cat.itacademy.blackjack.game_statistics.infrastructure.jpa.mapper;

import cat.itacademy.blackjack.common.domain.value_object.Name;
import cat.itacademy.blackjack.game_statistics.domain.model.FinishedGame;
import cat.itacademy.blackjack.game_statistics.domain.model.PlayerProfile;
import cat.itacademy.blackjack.game_statistics.infrastructure.jpa.entity.JpaFinishedGameEntity;
import cat.itacademy.blackjack.game_statistics.infrastructure.jpa.entity.JpaPlayerProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlayerProfileMapper {
    private final FinishedGameMapper finishedGameMapper;

    public JpaPlayerProfileEntity toEntity(PlayerProfile playerProfile){

        return JpaPlayerProfileEntity.builder()
                .id((playerProfile.getId() != null) ? null : playerProfile.getId())
                .name(playerProfile.getName().value())
                .numberOfGamesWon(playerProfile.getNumberOfGamesWon())
                .totalGamesPlayed(playerProfile.getNumberOfGamesPlayed())
                .score(playerProfile.getScore()).build();
    }

    public PlayerProfile toDomain (JpaPlayerProfileEntity entity){
        return new PlayerProfile(
                entity.getId(),
                Name.of(entity.getName()),
                entity.getNumberOfGamesWon(),
                entity.getTotalGamesPlayed(),
                entity.getScore()
        );
    }
}
