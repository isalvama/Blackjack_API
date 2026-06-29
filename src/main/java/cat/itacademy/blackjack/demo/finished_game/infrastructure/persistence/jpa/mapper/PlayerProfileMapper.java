package cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.mapper;

import cat.itacademy.blackjack.demo.common.domain.value_object.Name;
import cat.itacademy.blackjack.demo.finished_game.domain.model.PlayerProfile;
import cat.itacademy.blackjack.demo.finished_game.infrastructure.persistence.jpa.entity.JpaPlayerProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public List<PlayerProfile> toDomain (List<JpaPlayerProfileEntity> entities){
        return entities.stream().map(this::toDomain).toList();
    }
}
