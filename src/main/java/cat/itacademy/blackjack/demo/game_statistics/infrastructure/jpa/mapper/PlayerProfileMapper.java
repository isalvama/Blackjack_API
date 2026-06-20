package cat.itacademy.blackjack.demo.game_statistics.infrastructure.jpa.mapper;

import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.game_statistics.domain.model.PlayerProfile;
import cat.itacademy.blackjack.demo.game_statistics.infrastructure.jpa.entity.JpaPlayerProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlayerProfileMapper {

    public JpaPlayerProfileEntity toEntity(PlayerProfile playerProfile){

        return new JpaPlayerProfileEntity(
                (playerProfile.getId() != null) ? playerProfile.getId() : null,
                playerProfile.getName().name(),
                playerProfile.getNumberOfGamesWon(),
                playerProfile.getNumberOfGamesPlayed(),
                playerProfile.getScore());
    }

    public PlayerProfile toDomain (JpaPlayerProfileEntity entity){
        return PlayerProfile.reconstitute(
                entity.getId(),
                Name.of(entity.getName()),
                entity.getTotalGamesPlayed(),
                entity.getNumberOfGamesWon(),
                entity.getScore()
        );
    }
}
