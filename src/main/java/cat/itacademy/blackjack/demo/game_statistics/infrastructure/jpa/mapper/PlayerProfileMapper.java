package cat.itacademy.blackjack.demo.game_statistics.infrastructure.jpa.mapper;

import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.game_statistics.domain.model.PlayerProfile;
import cat.itacademy.blackjack.demo.game_statistics.infrastructure.jpa.entity.JpaPlayerProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
